package aionem.net.sdk.web.system.servlets;

import aionem.net.sdk.web.beans.ApiRes;
import aionem.net.sdk.web.servlets.HttpServletApi;
import aionem.net.sdk.web.servlets.PostMapping;
import aionem.net.sdk.web.system.dao.DaoSysDeploy;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(value={"/api/ui.system/deploy"}, name="ServletSysDeploy", description="Ui.System • AIONEM.net - Deploy Servlet")
public class ServletSysDeploy extends HttpServletApi {


    @PostMapping("/")
    protected void doPostDeploy(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        final ApiRes apiRes = new ApiRes();

        final boolean isDeployWar = DaoSysDeploy.deployWar("prod", "aionem.net.war");

        if(isDeployWar) {
            apiRes.setSuccess(true);
            apiRes.setMessage("Deploy successfully");
        }else {
            apiRes.setError("Deploy failed");
        }

        apiRes.setResponse(response);
    }

}
