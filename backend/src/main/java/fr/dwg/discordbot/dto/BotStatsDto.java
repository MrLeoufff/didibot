package fr.dwg.discordbot.dto;

import java.util.List;

public class BotStatsDto {

    private long repliesToday;
    private long repliesLast7Days;
    private long repliesAllTime;
    private long activeTriggers;
    private long pendingTriggers;
    private long serverCount;
    private List<NameCountDto> topTriggers = List.of();
    private List<NameCountDto> topUsers = List.of();

    public long getRepliesToday() {
        return repliesToday;
    }

    public void setRepliesToday(long repliesToday) {
        this.repliesToday = repliesToday;
    }

    public long getRepliesLast7Days() {
        return repliesLast7Days;
    }

    public void setRepliesLast7Days(long repliesLast7Days) {
        this.repliesLast7Days = repliesLast7Days;
    }

    public long getRepliesAllTime() {
        return repliesAllTime;
    }

    public void setRepliesAllTime(long repliesAllTime) {
        this.repliesAllTime = repliesAllTime;
    }

    public long getActiveTriggers() {
        return activeTriggers;
    }

    public void setActiveTriggers(long activeTriggers) {
        this.activeTriggers = activeTriggers;
    }

    public long getPendingTriggers() {
        return pendingTriggers;
    }

    public void setPendingTriggers(long pendingTriggers) {
        this.pendingTriggers = pendingTriggers;
    }

    public long getServerCount() {
        return serverCount;
    }

    public void setServerCount(long serverCount) {
        this.serverCount = serverCount;
    }

    public List<NameCountDto> getTopTriggers() {
        return topTriggers;
    }

    public void setTopTriggers(List<NameCountDto> topTriggers) {
        this.topTriggers = topTriggers;
    }

    public List<NameCountDto> getTopUsers() {
        return topUsers;
    }

    public void setTopUsers(List<NameCountDto> topUsers) {
        this.topUsers = topUsers;
    }
}
