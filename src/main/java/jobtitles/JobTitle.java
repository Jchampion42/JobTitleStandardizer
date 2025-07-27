package jobtitles;

/**
 * A pre-set list of job titles, with a string value for human-readable output
 */
public enum JobTitle {
    ARCHITECT("Architect","architect"),
    SOFTWARE_ENGINEER("Software engineer","software engineer"),
    QUANTITY_SURVEYOR("Quantity surveyor","quantity surveyor"),
    ACCOUNTANT("Accountant", "accountant");

    private String displayName;
    private String simpleName;
    JobTitle(String displayName, String simpleName) {
        this.displayName = displayName;
        this.simpleName = simpleName;
    }

    /**
     * The human-readable name of the job position
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * The lower case name of the job position
     */
    public String getSimpleName() {
        return simpleName;
    }
}
