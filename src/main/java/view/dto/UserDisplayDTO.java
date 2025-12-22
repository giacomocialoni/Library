package view.dto;

import bean.UserBean;

public class UserDisplayDTO {
    private final UserBean user;
    private final String lastPurchaseInfo;
    private final String lastLoanInfo;
    private final String statsInfo;
    
    public UserDisplayDTO(UserBean user, String lastPurchaseInfo, String lastLoanInfo, String statsInfo) {
        this.user = user;
        this.lastPurchaseInfo = lastPurchaseInfo;
        this.lastLoanInfo = lastLoanInfo;
        this.statsInfo = statsInfo;
    }
    
    // Getters
    public UserBean getUser() { return user; }
    public String getLastPurchaseInfo() { return lastPurchaseInfo; }
    public String getLastLoanInfo() { return lastLoanInfo; }
    public String getStatsInfo() { return statsInfo; }
}