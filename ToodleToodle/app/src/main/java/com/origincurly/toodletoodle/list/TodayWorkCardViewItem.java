package com.origincurly.toodletoodle.list;

import org.threeten.bp.LocalDateTime;

import java.util.List;

public class TodayWorkCardViewItem {

    public int id;

    public int importance; //1이면 별 1개, 2면 별 2개, 3이면 별 3개
    public String projectTitle;
    public String cardViewTitle;
    public List<TodayWorkToDoItem> toDoItems;

    public LocalDateTime startAt;
    public LocalDateTime endAt;

}
