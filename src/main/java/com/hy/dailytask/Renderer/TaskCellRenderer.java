package com.hy.dailytask.Renderer;

import com.hy.dailytask.enums.ProgressStatusEnum;
import com.hy.dailytask.model.TimeResultModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TaskCellRenderer extends DefaultTableCellRenderer {
    @Override
    protected void setValue(Object value) {
        if (value instanceof TimeResultModel) {
            TimeResultModel remainingTime = (TimeResultModel) value;
            setText(remainingTime.getLocalTimeStr());
            ProgressStatusEnum progressStatusEnum = remainingTime.getProgressStatusEnum();
            if (progressStatusEnum.equals(ProgressStatusEnum.NOTSTART)){
                setForeground(Color.LIGHT_GRAY);
            }else if (progressStatusEnum.equals(ProgressStatusEnum.START)){
                setForeground(Color.YELLOW);
            }else if (progressStatusEnum.equals(ProgressStatusEnum.FINISH)){
                setForeground(Color.GRAY);
            }else if (progressStatusEnum.equals(ProgressStatusEnum.TIMEOUT)){
                setForeground(Color.RED);
            }
            setHorizontalAlignment(JLabel.CENTER);
        } else if (value instanceof String) {
            String status = (String) value;
            setText(status);
            if (status.equals(ProgressStatusEnum.NOTSTART.getValue())){
                setForeground(Color.LIGHT_GRAY);
            }else if (status.equals(ProgressStatusEnum.START.getValue())){
                setForeground(Color.YELLOW);
            }else if (status.equals(ProgressStatusEnum.FINISH.getValue())){
                setForeground(Color.GREEN);
            }else if (status.equals(ProgressStatusEnum.TIMEOUT.getValue())){
                setForeground(Color.RED);
            }
            setHorizontalAlignment(JLabel.CENTER);
        } else {
            super.setValue(value);
        }
    }
}



