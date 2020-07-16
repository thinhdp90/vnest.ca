package ai.kitt.snowboy.api.model;

public class ActiveCode {
    private String phone, activationCode;

    public ActiveCode(String phone, String activationCode) {
        this.phone = phone;
        this.activationCode = activationCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }
}
