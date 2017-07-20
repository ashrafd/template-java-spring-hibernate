/*
 * Copyright (c) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.controller;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageAuthorDetails;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;
import com.google.api.services.youtube.model.LiveChatMessageSnippet;
import com.google.api.services.youtube.model.LiveChatSuperChatDetails;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Lists live chat messages and SuperChat details from a live broadcast.
 * <p>
 * The videoId is often included in the video's url, e.g.:
 * https://www.youtube.com/watch?v=L5Xc93_ZL60
 * ^ videoId
 * The video URL may be found in the browser address bar, or by right-clicking a video and selecting
 * Copy video URL from the context menu.
 *
 * @author Jim Rogers
 */
public class ListLiveChatMessages {

    /**
     * Common fields to retrieve for chat messages
     */
    private List<LiveChatMessage> liveChatMessageList = new ArrayList<LiveChatMessage>();
    private static final String LIVE_CHAT_FIELDS =
            "items(authorDetails(channelId,displayName,isChatModerator,isChatOwner,isChatSponsor,"
                    + "profileImageUrl),snippet(displayMessage,superChatDetails,publishedAt)),"
                    + "nextPageToken,pollingIntervalMillis";

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * Lists live chat messages and SuperChat details from a live broadcast.
     * <p>
     * from the chat associated with this video. If the videoId is not specified, the signed in
     * user's current live broadcast will be used instead.
     */
    public List<LiveChatMessage> grabLiveMessages(String videoId) {

        // This OAuth 2.0 access scope allows for read-only access to the
        // authenticated user's account, but not other types of account access.
        List<String> scopes = Lists.newArrayList(YouTubeScopes.YOUTUBE_READONLY);
        try {
            // Authorize the request.
            Credential credential = Auth.authorize(scopes, "listlivechatmessages");

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                    .setApplicationName("youtube-cmdline-listchatmessages-sample").build();

            // Get the liveChatId
            String liveChatId = GetLiveChatId.getLiveChatId(youtube, videoId);

            // Get live chat messages
            listChatMessages(liveChatId, null, 0);
        } catch (GoogleJsonResponseException e) {
            System.err
                    .println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                            + e.getDetails().getMessage());
            e.printStackTrace();

        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
        return liveChatMessageList;
    }

    /**
     * Lists live chat messages, polling at the server supplied interval. Owners and moderators of a
     * live chat will poll at a faster rate.
     *
     * @param liveChatId    The live chat id to list messages from.
     * @param nextPageToken The page token from the previous request, if any.
     * @param delayMs       The delay in milliseconds before making the request.
     */
    public void listChatMessages(
            final String liveChatId,
            final String nextPageToken,
            long delayMs) {
        System.out.println(
                String.format("Getting chat messages in %1$.3f seconds...", delayMs * 0.001));
        Timer pollTimer = new Timer();
        pollTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            // Get chat messages from YouTube
                            LiveChatMessageListResponse response = youtube
                                    .liveChatMessages()
                                    .list(liveChatId, "snippet, authorDetails")
                                    .setPageToken(nextPageToken)
                                    .setFields(LIVE_CHAT_FIELDS)
                                    .execute();

                            // Display messages and super chat details
                            List<LiveChatMessage> messages = response.getItems();
                            for (int i = 0; i < messages.size(); i++) {
                                LiveChatMessage message = messages.get(i);
                            }
                            liveChatMessageList.addAll(messages);
                        } catch (Throwable t) {
                            System.err.println("Throwable: " + t.getMessage());
                            t.printStackTrace();
                        }
                    }
                }, delayMs);
    }

    /**
     * Formats a chat message for console output.
     *
     * @param message          The display message to output.
     * @param author           The author of the message.
     * @param superChatDetails SuperChat details associated with the message.
     * @return A formatted string for console output.
     */

    private static String buildOutput(
            String message,
            LiveChatMessageAuthorDetails author,
            LiveChatSuperChatDetails superChatDetails) {
        StringBuilder output = new StringBuilder();
        if (superChatDetails != null) {
            output.append(superChatDetails.getAmountDisplayString());
            output.append("SUPERCHAT RECEIVED FROM ");
        }
        output.append(author.getDisplayName());
        List<String> roles = new ArrayList<String>();
        if (author.getIsChatOwner()) {
            roles.add("OWNER");
        }
        if (author.getIsChatModerator()) {
            roles.add("MODERATOR");
        }
        if (author.getIsChatSponsor()) {
            roles.add("SPONSOR");
        }
        if (roles.size() > 0) {
            output.append(" (");
            String delim = "";
            for (String role : roles) {
                output.append(delim).append(role);
                delim = ", ";
            }
            output.append(")");
        }
        if (message != null && !message.isEmpty()) {
            output.append(": ");
            output.append(message);
        }
        return output.toString();
    }
}
