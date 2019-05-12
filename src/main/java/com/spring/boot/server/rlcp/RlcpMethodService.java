package com.spring.boot.server.rlcp;

import org.springframework.stereotype.Service;
import rlcp.echo.RlcpEchoRequest;
import rlcp.echo.RlcpEchoRequestBody;
import rlcp.generate.GeneratingResult;
import rlcp.generate.RlcpGenerateRequest;
import rlcp.generate.RlcpGenerateRequestBody;
import rlcp.generate.RlcpGenerateResponse;

@Service
public class RlcpMethodService {

  public static GeneratingResult getGenerate(String algorithm, String url) {
    RlcpGenerateRequestBody body = new RlcpGenerateRequestBody(algorithm);
    RlcpGenerateRequest rlcpGenerateRequest = body.prepareRequest(url);
    RlcpGenerateResponse rlcpResponse = null;
    try {
      rlcpResponse = rlcpGenerateRequest.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
    GeneratingResult result = rlcpResponse.getBody().getGeneratingResult();
    System.out.println("GENERATE successfully with result: {\n" +
        "\tcode: \"" + result.getCode() + "\",\n" +
        "\ttext: \"" + result.getText() + "\",\n" +
        "\tinstraction: \"" + result.getInstructions() + "\"}");
    return result;
  }

  private static boolean status(String url) {
    RlcpEchoRequestBody body = new RlcpEchoRequestBody();
    RlcpEchoRequest rlcpEchoRequest = body.prepareRequest(url);
    try {
      rlcpEchoRequest.execute();
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
