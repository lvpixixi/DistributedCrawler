package bscrawler.spider.geetest;

/**
 * Created by  yuananyun on 2017/1/14.
 */
public class GeetestValidation {

    private String code;
    private String message;
    private String geetest_challenge;

    private String geetest_validate;
    private String geetest_seccode;


    public GeetestValidation() {
    }

    public GeetestValidation(String code) {
        this.code = code;
    }

    public GeetestValidation(String geetest_challenge, String geetest_validate, String geetest_seccode) {
        this.geetest_challenge = geetest_challenge;
        this.geetest_validate = geetest_validate;
        this.geetest_seccode = geetest_seccode;
    }


    public String getGeetest_challenge() {
        return geetest_challenge;
    }

    public void setGeetest_challenge(String geetest_challenge) {
        this.geetest_challenge = geetest_challenge;
    }

    public String getGeetest_validate() {
        return geetest_validate;
    }

    public void setGeetest_validate(String geetest_validate) {
        this.geetest_validate = geetest_validate;
    }

    public String getGeetest_seccode() {
        if (geetest_validate != null) {
            return geetest_validate + "|jordan";
        }
        return geetest_seccode;
    }

    public void setGeetest_seccode(String geetest_seccode) {
        this.geetest_seccode = geetest_seccode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "GeetestValidation{" +
                "code='" + code + '\'' +
                ", message='" + getMessage() + '\'' +
                ", geetest_challenge='" + geetest_challenge + '\'' +
                ", geetest_validate='" + geetest_validate + '\'' +
                ", geetest_seccode='" + geetest_seccode + '\'' +
                '}';
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
