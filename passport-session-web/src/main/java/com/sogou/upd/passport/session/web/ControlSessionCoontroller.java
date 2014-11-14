package com.sogou.upd.passport.session.web;

import com.alibaba.fastjson.JSONObject;
import com.sogou.upd.passport.session.model.DeleteSessionParams;
import com.sogou.upd.passport.session.model.SetSessionParams;
import com.sogou.upd.passport.session.services.SessionService;
import com.sogou.upd.passport.session.util.CodeUtil;
import com.sogou.upd.passport.session.util.ControllerHelper;
import com.sogou.upd.passport.session.util.SessionServerUtil;
import org.apache.commons.lang3.StringUtils;
import org.perf4j.StopWatch;
import org.perf4j.aop.Profiled;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 负责设置session相关的处理
 * User: ligang201716@sogou-inc.com
 * Date: 13-11-27
 * Time: 下午2:06
 */
@Controller
public class ControlSessionCoontroller extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(ControlSessionCoontroller.class);

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/set_session",params={"client_id=1120"}, method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String setSession(HttpServletRequest request,SetSessionParams setSessionParams){
        StopWatch stopWatch = new Slf4JStopWatch(WebTimingLogger);
        request.setAttribute(STOPWATCH, stopWatch);
        request.setAttribute(SLOW_THRESHOLD, 20);


        JSONObject result=new JSONObject();
        // 参数校验
        String validateResult = ControllerHelper.validateParams(setSessionParams);
        if(StringUtils.isNotBlank(validateResult)){
            result.put("status","10002");
            result.put("statusText",validateResult);
            return handleResult(result,request);
        }

        if(!SessionServerUtil.checkSid(setSessionParams.getSgid())){
            result.put("status","50002");
            result.put("statusText","sid自校验错误");
            return handleResult(result,request);
        }

        if(!CodeUtil.checkCode(setSessionParams)){
            result.put("status","10003");
            result.put("statusText","code签名错误");
            return handleResult(result,request);
        }

        sessionService.setSession(setSessionParams.getSgid(),setSessionParams.getUser_info());

        result.put("status","0");

        return handleResult(result,request);
    }

    @RequestMapping(value = "/del_session",params={"client_id=1120"}, method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String deleteSession(HttpServletRequest request,DeleteSessionParams deleteSessionParams){
        StopWatch stopWatch = new Slf4JStopWatch(WebTimingLogger);
        request.setAttribute("stopWatch", stopWatch);

        JSONObject result=new JSONObject();
        // 参数校验
        String validateResult = ControllerHelper.validateParams(deleteSessionParams);
        if(StringUtils.isNotBlank(validateResult)){
            result.put("status","10002");
            result.put("statusText",validateResult);
            return handleResult(result,request);
        }

        if(!CodeUtil.checkCode(deleteSessionParams)){
            result.put("status","10003");
            result.put("statusText","code签名错误");
            return handleResult(result,request);
        }

        sessionService.deleteSession(deleteSessionParams.getSgid());

        result.put("status","0");

        return handleResult(result,request);
    }

}
