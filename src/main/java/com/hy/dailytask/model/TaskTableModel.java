package com.hy.dailytask.model;

import com.alibaba.fastjson2.JSON;
import com.hy.dailytask.entry.Task;

import javax.swing.table.AbstractTableModel;
import java.sql.Array;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskTableModel extends AbstractTableModel {
    private final List<Task> tasks;
    private final String[] columnNames = {"Task Name", "Time Remaining", "Status","Task Period"};


    public TaskTableModel(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public int getRowCount() {
        return tasks.size();
    }


    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Task> getTasks(){
        return this.tasks;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Task task = tasks.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return task.getContent();
            case 1:
                return task.getRemainingTime();
            case 2:
                return task.getStatus();
            case 3:
                return task.getTimePeriod();

            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return Duration.class;
            case 2:
                return String.class;
            case 3:
                return String.class;

            default:
                return Object.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (tasks.get(rowIndex).isCompleted()){
            return false;
        }else {
            return columnIndex == 0;
        }
    }


    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Task task = tasks.get(rowIndex);
        if (columnIndex == 0) {
            task.setContent((String) aValue);
        } else if (columnIndex == 3) {
            task.setCompleted((Boolean) aValue);
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }


    public int getStatusColumnIndex() {
        for (int i = 0; i < getColumnCount(); i++) {
            if (columnNames[i].equals("Status")) {
                return i;
            }
        }
        return -1; // 如果没有找到状态列，返回 -1
    }

}