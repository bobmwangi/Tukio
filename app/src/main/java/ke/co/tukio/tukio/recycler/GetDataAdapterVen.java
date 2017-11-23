package ke.co.tukio.tukio.recycler;


public class GetDataAdapterVen {

    public String ImageServerUrl;
    public String  VenName, VenId, VenLoc, KickoValue;

    public String getImageServerUrl() {
        return ImageServerUrl;
    }
    public void setImageServerUrl(String imageServerUrl) {
        this.ImageServerUrl = imageServerUrl;
    }


    //NAME
    public String getVenName() {
        return VenName;
    }
    public void setVenName(String VenName) {
        this.VenName = VenName;
    }

    //ID
    public String getVenId() {
        return VenId;
    }
    public void setVenId(String VenId) {
        this.VenId = VenId;
    }

    //LOC
    public String getVenLoc() {
        return VenLoc;
    }
    public void setVenLoc(String VenLoc) {
        this.VenLoc = VenLoc;
    }

    //KICKOVALUE
    public String getKicko() {
        return KickoValue;
    }
    public void setKicko(String KickoValue) {
        this.KickoValue = KickoValue;
    }
}
