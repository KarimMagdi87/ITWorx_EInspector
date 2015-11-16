package itworx.com.e_inspector;

public class Case {
    public String agentId ;
    public String caseNumber ;
    public String description ;
    public String endTime ;
    public String incidentAudioURI ;
    public String incidentImageURI ;
    public String latitude ;
    public String longitude ;
    public String resolutionComment ;
    public String resolutionImageURI ;
    public String startTime ;
    public String status ;
    public String title ;

    @Override
    public String toString() {
        return "Case [agentId=" + agentId
                + ", latitude=" + latitude + ", longitude=" + longitude
                + ", incidentAudioURI=" + incidentAudioURI + ", incidentImageURI=" + incidentImageURI
                + ", description=" + description + ", status=" + status
                + ", title=" + title + "]";
    }
}
