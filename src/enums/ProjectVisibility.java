/*
 * ProjectVisibility.java
 * This enum represents the visibility of a project.
 * It can be either visible or hidden.
 */

package enums;

public enum ProjectVisibility {
    VISIBILE("Visible"),
    HIDDEN("Hidden");
    private final String visibility;

    ProjectVisibility(String visibility){
        this.visibility = visibility;
    }
    public String getvisibility() {
        return visibility;
    }
}
