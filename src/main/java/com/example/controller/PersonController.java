package com.example.controller;

import com.example.controller.ListLiveChatMessages;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageAuthorDetails;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;
import com.google.api.services.youtube.model.LiveChatMessageSnippet;
import com.google.api.services.youtube.model.LiveChatSuperChatDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/index")
public class PersonController {


    private static YouTube youtube;

    @RequestMapping(method = RequestMethod.GET)
    public String printHello(ModelMap model, @RequestParam(value = "videoId", required = false) String videoId) {

     /*   ListLiveChatMessages listLiveChatMessages = new ListLiveChatMessages();
        List<LiveChatMessage> liveChatMessages = new ArrayList<LiveChatMessage>();
        //Youtubesearch youtubesearch = new Youtubesearch();

        try {
            liveChatMessages  = listLiveChatMessages.grabLiveMessages(videoId);
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        model.put("videoId", videoId);
        model.put("liveMessages", liveChatMessages);
        */
        return "index";
    }


}