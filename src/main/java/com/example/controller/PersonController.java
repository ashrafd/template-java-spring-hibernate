package com.example.controller;

import com.google.api.services.youtube.YouTube;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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