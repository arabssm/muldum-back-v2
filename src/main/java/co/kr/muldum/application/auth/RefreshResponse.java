package co.kr.muldum.application.auth;

public class RefreshResponse {
  private final String access_token;

  public RefreshResponse(String access_token) {
    this.access_token = access_token;
  }
}
