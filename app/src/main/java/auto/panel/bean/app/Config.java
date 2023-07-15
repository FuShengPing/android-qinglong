package auto.panel.bean.app;

/**
 * @author wsfsp4
 * @version 2023.07.14
 */
public class Config {
    private String documentUrl;
    private String giteeUrl;
    private String githubUrl;
    private String groupKey;
    private String shareText;

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getGiteeUrl() {
        return giteeUrl;
    }

    public void setGiteeUrl(String giteeUrl) {
        this.giteeUrl = giteeUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getShareText() {
        return shareText;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }
}
