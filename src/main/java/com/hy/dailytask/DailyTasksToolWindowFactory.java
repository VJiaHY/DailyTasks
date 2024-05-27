package com.hy.dailytask;


import com.hy.dailytask.enums.ProgressStatusEnum;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.Alarm;


import com.hy.dailytask.Renderer.TaskCellRenderer;
import com.hy.dailytask.model.TaskTableModel;
import com.hy.dailytask.entry.Task;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DailyTasksToolWindowFactory implements ToolWindowFactory, DumbAware {
    private final List<Task> tasks = new ArrayList<>();
    private final TaskTableModel taskTableModel = new TaskTableModel(tasks);
    private final Alarm alarm = new Alarm();

    private LocalDate localDate;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        //创建主面板
        JPanel panel = new JPanel(new BorderLayout());
        JTable taskTable = new JTable(taskTableModel);
        taskTable.setFillsViewportHeight(true);

        // 使用自定义渲染器和编辑器
        taskTable.getColumnModel().getColumn(1).setCellRenderer(new TaskCellRenderer());
        taskTable.getColumnModel().getColumn(2).setCellRenderer(new TaskCellRenderer());
        taskTable.getColumnModel().getColumn(3).setCellRenderer(new TaskCellRenderer());
        panel.add(new JScrollPane(taskTable), BorderLayout.CENTER);

        //创建所需输入框和按钮
        JTextField taskField = createHintTextField("任务名称");
        JTextField startTimeField = createHintTextField("开始时间 (HH:mm)");
        JTextField endTimeField = createHintTextField("结束时间 (HH:mm)");
        JButton addButton = new JButton(" 添加新任务");
        JButton removeButton = new JButton("删除已完成");

        //新增任务按钮触发事件
        addButton.addActionListener(e -> {
            String taskContent = taskField.getText().trim();
            String startTimeStr = startTimeField.getText().trim();
            String endTimeStr = endTimeField.getText().trim();
            try {
                LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime endTime = LocalTime.parse(endTimeStr, DateTimeFormatter.ofPattern("HH:mm"));

                if (!taskContent.isEmpty() && startTime.isBefore(endTime)) {
                    Task task = new Task(System.currentTimeMillis(), taskContent, startTime, endTime, startTime + "~" + endTime);
                    tasks.add(task);
                    taskTableModel.fireTableDataChanged();
                    resetHintTextField(taskField, "任务名称");
                    resetHintTextField(startTimeField, "开始时间 (HH:mm)");
                    resetHintTextField(endTimeField, "结束时间 (HH:mm)");
                } else {
                    JOptionPane.showMessageDialog(panel, "请输入有效的任务详细信息");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "时间格式无效。使用 HH:mm");
            }
            localDate = LocalDate.now();
        });
        //删除已完成任务按钮事件
        removeButton.addActionListener(e -> {
            //对话框
            int result = JOptionPane.showConfirmDialog(panel, "是否删除已完成任务?", "确认", JOptionPane.YES_NO_OPTION);
            //如果选择是返回的则是 0
            if (result == JOptionPane.YES_OPTION) {
                tasks.removeIf(Task::isCompleted);
                taskTableModel.fireTableDataChanged();
            }
        });
        changeStyle(toolWindow, taskField, startTimeField, endTimeField, addButton, removeButton, panel);
        // 设置定时提醒
        alarm.addRequest(() -> checkIncompleteTasks(project), 300 * 1000); // 半小时检查一次

        // 定时刷新表格
        Timer timer = new Timer(1000, e -> flushedTable(project));
        timer.start();

        //点击事件
        taskTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = taskTable.getSelectedRow();
                int statusColumnIndex = taskTableModel.getStatusColumnIndex();
                //如果点击的列为 完成状态列
                if (statusColumnIndex != -1 && row >= 0) {
                    //获得索引
                    int column = taskTable.convertColumnIndexToModel(taskTable.getSelectedColumn());
                    if (column == statusColumnIndex) {
                        //根据索引获得对象
                        Task task = tasks.get(row);
                        if (!task.getStatus().equals(ProgressStatusEnum.FINISH.getValue())) {
                            int result = JOptionPane.showConfirmDialog(panel, "是否标记任务为已完成?", "确认", JOptionPane.YES_NO_OPTION);
                            if (result == JOptionPane.YES_OPTION) {
                                //状态更改
                                task.setCompleted(true);
                                //刷新列表
                                taskTableModel.fireTableDataChanged();
                            }
                        }
                    }
                }
            }
        });
    }
    /**
     * 修改输入框和按钮样式
     */
    private void changeStyle(@NotNull ToolWindow toolWindow, JTextField taskField, JTextField startTimeField, JTextField endTimeField, JButton addButton, JButton removeButton, JPanel panel) {
        //按钮
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 设置任务名称输入框
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(taskField, gbc);
        setFixedSize(taskField);

        // 设置开始时间输入框
        gbc.gridx = 1;
        gbc.weightx = 0.2;
        inputPanel.add(startTimeField, gbc);
        setFixedSize(startTimeField);

        // 设置结束时间输入框
        gbc.gridx = 2;
        gbc.weightx = 0.2;
        inputPanel.add(endTimeField, gbc);
        setFixedSize(endTimeField);

        // 设置添加按钮
        gbc.gridx = 3;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(addButton, gbc);
        setFixedSize(addButton);

        // 设置删除按钮
        gbc.gridx = 4;
        gbc.weightx = 0;
        inputPanel.add(removeButton, gbc);
        setFixedSize(removeButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    /**
     * 设置固定大小 防止 版本不兼容问题 格式在聚焦之后变小
     * @param component
     */
    private void setFixedSize(JComponent component) {
        Dimension size = component.getPreferredSize();
        component.setMinimumSize(size);
        component.setMaximumSize(size);
        component.setPreferredSize(size);
    }

    /**
     * 输入框提示样式,将任务名称 开始时间等提示,在点击的时候隐藏
     * @param hint
     * @return
     */
    private JTextField createHintTextField(String hint) {
        JTextField textField = new JTextField(hint);
        Color defaultColor = textField.getForeground();
        textField.setForeground(Color.GRAY);

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(hint)) {
                    textField.setText("");
                    textField.setForeground(defaultColor);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(hint);
                }
            }
        });
        // 设置输入框的首选大小
        Dimension preferredSize = textField.getPreferredSize();
        textField.setMinimumSize(preferredSize);
        textField.setMaximumSize(preferredSize);
        textField.setPreferredSize(preferredSize);

        return textField;
    }


    private void resetHintTextField(JTextField textField, String hint) {
        textField.setForeground(Color.GRAY);
        textField.setText(hint);
    }


    /**
     * 方法主要作用可以及时发现已经超时的任务
     * 可以让 剩余时间列的秒进行刷新
     * @param project
     */    private void flushedTable(@NotNull Project project) {
        LocalTime now = LocalTime.now();
        //如果第二天 清空列表
        if (null == localDate || localDate.isBefore(LocalDate.now())){
            tasks.clear();
        }
        //过滤出已经超时 但是还没通知过的任务
        List<Task> overdueTasks = tasks.stream()
                .filter(task -> !task.isCompleted() && now.isAfter(task.getEndTime()) && !task.getNotified())
                .collect(Collectors.toList());

        List<Long> taskIds = new ArrayList<>();
        if (!overdueTasks.isEmpty()) {
            //获取任务名称并用 ',' 分割
            String message = overdueTasks.stream()
                    .map(t -> {
                        taskIds.add(t.getId());
                        return t.getContent();
                    })
                    .collect(Collectors.joining(", "));

            //发送提醒
            Notification notification = NotificationGroupManager.getInstance()
                    .getNotificationGroup("taskGroup")
                    .createNotification("每日任务提醒", "您有新的超时任务: " + message, NotificationType.INFORMATION);
            notification.notify(project);
        }
        //将上面通知过的任务该为已通知
        tasks.forEach(t -> {
            if (taskIds.contains(t.getId())) {
                t.setNotify(true);
            }
        });
        //刷新列表
        taskTableModel.fireTableDataChanged();
    }
    /**
     * 半小时检索列表 并且找出所有已经超时的任务并进行通知
     * @param project
     */
    private void checkIncompleteTasks(@NotNull Project project) {
        LocalTime now = LocalTime.now();
        List<Task> overdueTasks = tasks.stream()
                .filter(task -> !task.isCompleted() && now.isAfter(task.getEndTime()))
                .collect(Collectors.toList());

        if (!overdueTasks.isEmpty()) {
            String message = overdueTasks.stream()
                    .map(Task::getContent)
                    .collect(Collectors.joining(", "));
            Notification notification = NotificationGroupManager.getInstance()
                    .getNotificationGroup("taskGroup")
                    .createNotification("每日任务提醒", "超时任务定期提醒:"+ message, NotificationType.INFORMATION);
            notification.notify(project);
        }

        alarm.addRequest(() -> checkIncompleteTasks(project), 30 * 1000); // 再次设置提醒
    }
}