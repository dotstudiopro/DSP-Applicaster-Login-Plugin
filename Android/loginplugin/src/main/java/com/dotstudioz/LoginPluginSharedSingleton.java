package com.dotstudioz;

import android.content.Context;
import android.util.Log;

import com.applicaster.hook_screen.HookScreenListener;
import com.dotstudioz.dotstudioPRO.models.dto.CustomFieldDTO;
import com.dotstudioz.dotstudioPRO.models.dto.SpotLightCategoriesDTO;
import com.dotstudioz.dotstudioPRO.models.dto.SpotLightChannelDTO;
import com.dotstudioz.dotstudioPRO.models.dto.VideoInfoDTO;
import com.dotstudioz.dotstudioPRO.services.constants.ApplicationConstantURL;
import com.dotstudioz.dotstudioPRO.services.constants.ApplicationConstants;
import com.dotstudioz.dotstudioPRO.services.services.CompanyTokenService;
import com.dotstudioz.dotstudioPRO.services.services.LatitudeAndLongitudeService;
import com.dotstudioz.dotstudioPRO.services.util.CommonServiceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginPluginSharedSingleton {

    private static String TAG = "LoginPluginSharedSingleton";

    private static final LoginPluginSharedSingleton ourInstance = new LoginPluginSharedSingleton();

    public static LoginPluginSharedSingleton getInstance() {
        return ourInstance;
    }

    private LoginPluginSharedSingleton() {
    }

    public HookScreenListener hookListener;

    //flag to hold the subscription check for the last channel loaded
    public boolean lastSubscriptionResult = false;

    public String ISO_CODE;
    public String COUNTRY;
    public String longitudeFloat;
    public String latitudeFloat;
    public String selectedChannelId;
    public String API_DOMAIN = "api.myspotlight.tv";


    public void initializeAPIDomain() {
        //PRODUCTION_URL
        ApplicationConstantURL.API_DOMAIN = "http://" + API_DOMAIN; //"http://api.myspotlight.tv"; //PRODUCTION SERVER
        ApplicationConstantURL.API_DOMAIN_S = "https://" + API_DOMAIN; //"https://api.myspotlight.tv"; //PRODUCTION SERVER

        //Reset all URL's
        ApplicationConstantURL.getInstance().setAPIDomain();
    }

    LatitudeAndLongitudeService latitudeAndLongitudeService;
    public void getLonLatCountry(Context context) {
        if(SPLTLoginPluginConstants.getInstance().strAccessToken != null && SPLTLoginPluginConstants.getInstance().strAccessToken.length() > 0) {
            if (latitudeAndLongitudeService == null)
                latitudeAndLongitudeService = new LatitudeAndLongitudeService(context);
            latitudeAndLongitudeService.setLatitudeAndLongitudeServiceListener(new LatitudeAndLongitudeService.LatitudeAndLongitudeInterface() {
                @Override
                public void latitudeAndLongitudeResponse(String ACTUAL_RESPONSE) {
                    try {
                        JSONObject resultJSONObject = new JSONObject(ACTUAL_RESPONSE);
                        if (resultJSONObject.has("data")) {
                            if (resultJSONObject.getJSONObject("data").has("countryCode")) {
                                LoginPluginSharedSingleton.getInstance().ISO_CODE = resultJSONObject.getJSONObject("data").getString("countryCode");
                                LoginPluginSharedSingleton.getInstance().COUNTRY = resultJSONObject.getJSONObject("data").getString("countryName");
                                Log.d(TAG, "getLonLatCountry==>"+LoginPluginSharedSingleton.getInstance().ISO_CODE + "-" + LoginPluginSharedSingleton.getInstance().ISO_CODE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void latitudeAndLongitudeError(String ERROR) {

                }
            });
            latitudeAndLongitudeService.getLatitudeAndLongitude(SPLTLoginPluginConstants.getInstance().strAccessToken, ApplicationConstantURL.getInstance().LON_LAT_COUNTRY);
        } else {
            CompanyTokenService companyTokenService = new CompanyTokenService(context);
            companyTokenService.setCompanyTokenServiceListener(new CompanyTokenService.ICompanyTokenService() {
                @Override
                public void companyTokenServiceResponse(JSONObject responseBody) {
                    try {
                        if(responseBody != null && responseBody.has("token")) {
                            SPLTLoginPluginConstants.getInstance().strAccessToken = responseBody.getString("token");

                            SharedPreferencesUtil.getInstance(context).addToSharedPreference(
                                    ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE,
                                    SPLTLoginPluginConstants.getInstance().strAccessToken,
                                    ApplicationConstants.TOKEN_RESPONSE_SHARED_PREFERENCE_KEY);

                            Log.d(TAG, "companyTokenServiceResponse: SPLTLoginPluginConstants.getInstance().strAccessToken==>"+SPLTLoginPluginConstants.getInstance().strAccessToken);

                            getLonLatCountry(context);
                        } else {
                            SPLTLoginPluginConstants.getInstance().strAccessToken = "";
                        }
                    } catch(Exception e) {
                        SPLTLoginPluginConstants.getInstance().strAccessToken = "";
                        e.printStackTrace();
                    }
                }

                @Override
                public void companyTokenServiceError(String responseBody) {
                    SPLTLoginPluginConstants.getInstance().strAccessToken = "";
                }
            });
            companyTokenService.requestForToken(SPLTLoginPluginConstants.apiKey, ApplicationConstantURL.getInstance().TOKEN_URL);
        }
    }

    public SpotLightChannelDTO getSpotLightChannelDTO(JSONObject response) {
        JSONObject obj = response;

        SpotLightChannelDTO spotLightChannelDTO = new SpotLightChannelDTO();
        try {

            //for (int i = 0; i < channelsArray.length(); i++) {
            try {
                JSONObject channel = obj.getJSONObject("data");
                JSONObject spotLightCategoriesDTOOBJ = new JSONObject();
                SpotLightCategoriesDTO spotLightCategoriesDTO = new SpotLightCategoriesDTO();
                spotLightChannelDTO.setId(channel.getString("_id"));
                try {
                    if (channel.has("is_product")) {
                        if (channel.getString("is_product") != null) {
                            if (channel.getString("is_product").equals("true"))
                                spotLightChannelDTO.setProduct(true);
                            else
                                spotLightChannelDTO.setProduct(false);
                        } else {
                            spotLightChannelDTO.setProduct(false);
                        }
                    }
                } catch (Exception em) {
                    em.printStackTrace();
                }

                spotLightChannelDTO.setTitle(channel.getString("title"));
                try {
                    try {
                        if (channel.has("poster")) {
                            String imageString = channel.getString("poster");
                            spotLightChannelDTO.setPoster(imageString);
                        }
                    } catch (JSONException e) {
                        spotLightChannelDTO.setPoster("");
                    }
                    try {
                        if (channel.has("dspro_id")) {
                            String dsproId = channel.getString("dspro_id");
                            spotLightChannelDTO.setDspro_id(dsproId);
                        }
                    } catch (JSONException e) {
                        spotLightChannelDTO.setDspro_id("");
                    }


                    String imageString = "";
                    try {
                        imageString = channel.getString("image");
                    } catch (JSONException e) {
                        imageString = channel.getString("poster");
                    }

                    imageString = CommonServiceUtils.replaceDotstudioproWithMyspotlightForImage(imageString);
                    spotLightChannelDTO.setImage(imageString);
                } catch (JSONException e) {
                    spotLightChannelDTO.setImage("");
                }
                try {
                    String imageString = channel.getString("spotlight_poster");
                    imageString = CommonServiceUtils.replaceDotstudioproWithMyspotlightForImage(imageString);
                    spotLightChannelDTO.setSpotlightImage(imageString);
                } catch (JSONException e) {
                    try {
                        String imageString = channel.getString("videos_thumb");
                        imageString = CommonServiceUtils.replaceDotstudioproWithMyspotlightForImage(imageString);
                        spotLightChannelDTO.setSpotlightImage(imageString);
                    } catch (JSONException ee) {
                        spotLightChannelDTO.setSpotlightImage("");
                    }
                    //e.printStackTrace();
                }

                try {
                    spotLightChannelDTO.setLink(channel.getString("link"));
                } catch (JSONException e) {
                    spotLightChannelDTO.setLink(channel.getString("channel_url"));
                }
                spotLightChannelDTO.setSlug(channel.getString("slug"));

                try {
                    spotLightChannelDTO.setChannelLogo(channel.getString("channel_logo"));
                } catch (JSONException e) { /*e.printStackTrace();*/ }
                try {
                    spotLightChannelDTO.setYear(channel.getString("year"));
                } catch (JSONException e) { /*e.printStackTrace();*/ }
                try {
                    spotLightChannelDTO.setLanguage(channel.getString("language"));
                } catch (JSONException e) { /*e.printStackTrace();*/ }
                try {
                    spotLightChannelDTO.setChannelRating(channel.getString("rating"));
                } catch (JSONException e) { /*e.printStackTrace();*/ }
                try {
                    spotLightChannelDTO.setCompany(channel.getString("company").toUpperCase());
                } catch (JSONException e) { /*e.printStackTrace();*/ }
                try {
                    spotLightChannelDTO.setSpotlight_company_id(channel.getString("spotlight_company_id"));
                } catch (JSONException e) { /*e.printStackTrace();*/ }
                try {
                    spotLightChannelDTO.setCountry(channel.getString("country"));
                } catch (JSONException e) { /*e.printStackTrace();*/ }
                try {
                    spotLightChannelDTO.setChannelDescription(channel.getString("description"));
                } catch (JSONException e) { /*e.printStackTrace();*/ }


                boolean isChildChannelPresent = false;
                JSONArray childChannelsArray = new JSONArray();
                try {
                    childChannelsArray = channel.getJSONArray("childchannels");
                    //childChannelsArray = channel.getJSONArray("playlist");
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
                if (childChannelsArray.length() > 0)
                    isChildChannelPresent = true;

                if (!isChildChannelPresent) {

                    boolean isVideo = false;
                    boolean isPlaylist = false;

                    try {
                        spotLightChannelDTO.setVideo(channel.getString("video_id"));
                        isVideo = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (channel.has("playlist")) {
                            if (channel.getJSONArray("playlist").length() > 0) {
                                isVideo = false;
                            } else {
                                if (!isVideo) {
                                    try {
                                        spotLightChannelDTO.setVideo(channel.getJSONObject("video").getString("_id"));
                                        isVideo = true;

                                        if (isVideo) {
                                            try {
                                                JSONArray playlistArray = channel.getJSONArray("playlist");
                                                try {
                                                    spotLightChannelDTO.getPlaylist().add(channel.getJSONObject("video").getString("_id"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                try {
                                                    VideoInfoDTO videoInfoDTO = new VideoInfoDTO();
                                                    videoInfoDTO.setVideoID(channel.getJSONObject("video").getString("_id"));
                                                    try {
                                                        videoInfoDTO.setVideoTitle(channel.getJSONObject("video").getString("title"));
                                                    } catch (Exception e) {
                                                    }
                                                    try {
                                                        videoInfoDTO.setSeriesTitle(channel.getJSONObject("video").getString("seriestitle"));
                                                    } catch (Exception e) {
                                                    }
                                                    try {
                                                        videoInfoDTO.setDescription(channel.getJSONObject("video").getString("description"));
                                                    } catch (Exception e) {
                                                    }
                                                    try {
                                                        videoInfoDTO.setThumb(channel.getJSONObject("video").getString("thumb"));
                                                    } catch (Exception e) {
                                                    }
                                                    try {
                                                        videoInfoDTO.setSlug(channel.getJSONObject("video").getString("slug"));
                                                    } catch (Exception e) {
                                                    }

                                                    try {
                                                        videoInfoDTO.setVideoYear(channel.getJSONObject("video").getString("year"));
                                                    } catch (JSONException e) {
                                                        videoInfoDTO.setVideoYear("-");
                                                    }
                                                    try {
                                                        videoInfoDTO.setVideoLanguage(channel.getJSONObject("video").getString("language"));
                                                    } catch (JSONException e) {
                                                        videoInfoDTO.setVideoLanguage("-");
                                                    }
                                                    try {
                                                        videoInfoDTO.setCountry(channel.getJSONObject("video").getString("country"));
                                                    } catch (JSONException e) {
                                                        videoInfoDTO.setCountry("-");
                                                    }

                                                    try {
                                                        String duraString = channel.getJSONObject("video").getString("duration");
                                                        float floatVideoDuration = Float.parseFloat(duraString);
                                                        int videoDurationInt = (int) floatVideoDuration;
                                                        videoInfoDTO.setVideoDuration(videoDurationInt);
                                                    } catch (Exception e) {
                                                        videoInfoDTO.setVideoDuration(0);
                                                    }
                                                    if (videoInfoDTO.getVideoPausedPoint() == 0) {
                                                        try {
                                                            int duraInt = channel.getJSONObject("video").getInt("duration");
                                                            videoInfoDTO.setVideoDuration(duraInt);
                                                        } catch (Exception e) {
                                                            videoInfoDTO.setVideoDuration(0);
                                                        }
                                                    }
                                                    if (videoInfoDTO.getVideoPausedPoint() == 0) {
                                                        try {
                                                            float floatVideoDuration = (float) (channel.getJSONObject("video").getDouble("duration"));
                                                            int videoDurationInt = (int) floatVideoDuration;
                                                            videoInfoDTO.setVideoDuration(videoDurationInt);
                                                        } catch (Exception e) {
                                                            videoInfoDTO.setVideoDuration(0);
                                                        }
                                                    }

                                                    try {
                                                        if (channel.getJSONObject("video").has("custom_fields")) {
                                                            for (int j = 0; j < channel.getJSONObject("video").getJSONArray("custom_fields").length(); j++) {
                                                                CustomFieldDTO customFieldDTO = new CustomFieldDTO();
                                                                if (((JSONObject) channel.getJSONObject("video").getJSONArray("custom_fields").get(j)).has("field_title"))
                                                                    customFieldDTO.setCustomFieldName(((JSONObject) channel.getJSONObject("video").getJSONArray("custom_fields").get(j)).getString("field_title"));
                                                                if (((JSONObject) channel.getJSONObject("video").getJSONArray("custom_fields").get(j)).has("field_value"))
                                                                    customFieldDTO.setCustomFieldValue(((JSONObject) channel.getJSONObject("video").getJSONArray("custom_fields").get(j)).getString("field_value"));
                                                                videoInfoDTO.getCustomFieldsArrayList().add(customFieldDTO);
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    spotLightChannelDTO.getVideoInfoDTOList().add(videoInfoDTO);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            if (!isVideo) {
                                try {
                                    spotLightChannelDTO.setVideo(channel.getJSONObject("video").getString("_id"));
                                    isVideo = true;

                                    if (isVideo) {
                                        try {
                                            JSONArray playlistArray = channel.getJSONArray("playlist");
                                            try {
                                                spotLightChannelDTO.getPlaylist().add(channel.getJSONObject("video").getString("_id"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            try {
                                                VideoInfoDTO videoInfoDTO = new VideoInfoDTO();
                                                videoInfoDTO.setVideoID(channel.getJSONObject("video").getString("_id"));
                                                try {
                                                    videoInfoDTO.setVideoTitle(channel.getJSONObject("video").getString("title"));
                                                } catch (Exception e) {
                                                }
                                                try {
                                                    videoInfoDTO.setSeriesTitle(channel.getJSONObject("video").getString("seriestitle"));
                                                } catch (Exception e) {
                                                }
                                                try {
                                                    videoInfoDTO.setDescription(channel.getJSONObject("video").getString("description"));
                                                } catch (Exception e) {
                                                }
                                                try {
                                                    videoInfoDTO.setThumb(channel.getJSONObject("video").getString("thumb"));
                                                } catch (Exception e) {
                                                }
                                                try {
                                                    videoInfoDTO.setSlug(channel.getJSONObject("video").getString("slug"));
                                                } catch (Exception e) {
                                                }

                                                try {
                                                    videoInfoDTO.setVideoYear(channel.getJSONObject("video").getString("year"));
                                                } catch (JSONException e) {
                                                    videoInfoDTO.setVideoYear("-");
                                                }
                                                try {
                                                    videoInfoDTO.setVideoLanguage(channel.getJSONObject("video").getString("language"));
                                                } catch (JSONException e) {
                                                    videoInfoDTO.setVideoLanguage("-");
                                                }
                                                try {
                                                    videoInfoDTO.setCountry(channel.getJSONObject("video").getString("country"));
                                                } catch (JSONException e) {
                                                    videoInfoDTO.setCountry("-");
                                                }

                                                try {
                                                    String duraString = channel.getJSONObject("video").getString("duration");
                                                    float floatVideoDuration = Float.parseFloat(duraString);
                                                    int videoDurationInt = (int) floatVideoDuration;
                                                    videoInfoDTO.setVideoDuration(videoDurationInt);
                                                } catch (Exception e) {
                                                    videoInfoDTO.setVideoDuration(0);
                                                }
                                                if (videoInfoDTO.getVideoPausedPoint() == 0) {
                                                    try {
                                                        int duraInt = channel.getJSONObject("video").getInt("duration");
                                                        videoInfoDTO.setVideoDuration(duraInt);
                                                    } catch (Exception e) {
                                                        videoInfoDTO.setVideoDuration(0);
                                                    }
                                                }
                                                if (videoInfoDTO.getVideoPausedPoint() == 0) {
                                                    try {
                                                        float floatVideoDuration = (float) (channel.getJSONObject("video").getDouble("duration"));
                                                        int videoDurationInt = (int) floatVideoDuration;
                                                        videoInfoDTO.setVideoDuration(videoDurationInt);
                                                    } catch (Exception e) {
                                                        videoInfoDTO.setVideoDuration(0);
                                                    }
                                                }

                                                try {
                                                    if (channel.getJSONObject("video").has("custom_fields")) {
                                                        for (int j = 0; j < channel.getJSONObject("video").getJSONArray("custom_fields").length(); j++) {
                                                            CustomFieldDTO customFieldDTO = new CustomFieldDTO();
                                                            if (((JSONObject) channel.getJSONObject("video").getJSONArray("custom_fields").get(j)).has("field_title"))
                                                                customFieldDTO.setCustomFieldName(((JSONObject) channel.getJSONObject("video").getJSONArray("custom_fields").get(j)).getString("field_title"));
                                                            if (((JSONObject) channel.getJSONObject("video").getJSONArray("custom_fields").get(j)).has("field_value"))
                                                                customFieldDTO.setCustomFieldValue(((JSONObject) channel.getJSONObject("video").getJSONArray("custom_fields").get(j)).getString("field_value"));
                                                            videoInfoDTO.getCustomFieldsArrayList().add(customFieldDTO);
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                spotLightChannelDTO.getVideoInfoDTOList().add(videoInfoDTO);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                    }

                    if (!isVideo) {
                        try {
                            JSONArray playlistArray = channel.getJSONArray("playlist");
                            for (int j = 0; j < playlistArray.length(); j++) {
                                VideoInfoDTO videoInfoDTO = new VideoInfoDTO();
                                videoInfoDTO.setChannelID(channel.getString("_id"));
                                if (((JSONObject) playlistArray.get(j)).has("_id"))
                                    videoInfoDTO.setVideoID(playlistArray.getJSONObject(j).getString("_id"));
                                if (((JSONObject) playlistArray.get(j)).has("thumb"))
                                    videoInfoDTO.setThumb(playlistArray.getJSONObject(j).getString("thumb"));
                                if (((JSONObject) playlistArray.get(j)).has("title"))
                                    videoInfoDTO.setVideoTitle(playlistArray.getJSONObject(j).getString("title"));
                                if (((JSONObject) playlistArray.get(j)).has("description"))
                                    videoInfoDTO.setDescription(playlistArray.getJSONObject(j).getString("description"));
                                if (((JSONObject) playlistArray.get(j)).has("seriestitle"))
                                    videoInfoDTO.setSeriesTitle(((JSONObject) playlistArray.get(j)).getString("seriestitle"));

                                String casting = "";
                                String writterDirector = "";
                                JSONArray castingArray = new JSONArray();
                                try {
                                    castingArray = playlistArray.getJSONObject(j).getJSONArray("actors");
                                } catch (Exception e) {
                                }
                                for (int k = 0; k < castingArray.length(); k++) {
                                    if (k == 0) {
                                        //casting = "Starring: " + castingArray.get(k).toString();
                                        casting = castingArray.get(k).toString();
                                    } else
                                        casting = casting + ", " + castingArray.get(k).toString();
                                }
                                videoInfoDTO.setCasting("");
                                if (casting.length() > 11)
                                    videoInfoDTO.setCasting(casting);

                                JSONArray writterDirectorArray = new JSONArray();
                                try {
                                    writterDirectorArray = playlistArray.getJSONObject(j).getJSONArray("directors");
                                } catch (Exception e) {
                                }
                                for (int k = 0; k < writterDirectorArray.length(); k++) {
                                    if (k == 0) {
                                        //writterDirector = "Written & Directed by " + writterDirectorArray.get(k).toString();
                                        writterDirector = writterDirectorArray.get(k).toString();
                                    } else
                                        writterDirector = writterDirector + ", " + writterDirectorArray.get(k).toString();
                                }
                                videoInfoDTO.setWritterDirector("");
                                if (writterDirector.length() > 23)
                                    videoInfoDTO.setWritterDirector(writterDirector);

                                if (((JSONObject) playlistArray.get(j)).has("slug"))
                                    videoInfoDTO.setSlug(playlistArray.getJSONObject(j).getString("slug"));

                                try {
                                    JSONObject vidInfoDTOJSONObject = (JSONObject) playlistArray.get(j);
                                    if (vidInfoDTOJSONObject.has("custom_fields")) {
                                        for (int k = 0; k < vidInfoDTOJSONObject.getJSONArray("custom_fields").length(); k++) {
                                            CustomFieldDTO customFieldDTO = new CustomFieldDTO();
                                            if (((JSONObject) vidInfoDTOJSONObject.getJSONArray("custom_fields").get(k)).has("field_title"))
                                                customFieldDTO.setCustomFieldName(((JSONObject) vidInfoDTOJSONObject.getJSONArray("custom_fields").get(k)).getString("field_title"));
                                            if (((JSONObject) vidInfoDTOJSONObject.getJSONArray("custom_fields").get(k)).has("field_value"))
                                                customFieldDTO.setCustomFieldValue(((JSONObject) vidInfoDTOJSONObject.getJSONArray("custom_fields").get(k)).getString("field_value"));
                                            videoInfoDTO.getCustomFieldsArrayList().add(customFieldDTO);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                spotLightChannelDTO.getVideoInfoDTOList().add(videoInfoDTO);

                            }
                            isPlaylist = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //channelDTOList.add(spotLightChannelDTO);
                } else {
                    spotLightChannelDTO.setIsSeasonsPresent(true);
                    spotLightChannelDTO.setNumberOfSeasons(childChannelsArray.length());
                    for (int c = 0; c < childChannelsArray.length(); c++) {
                        JSONObject childChannel = childChannelsArray.getJSONObject(c);
                        SpotLightChannelDTO childSpotLightChannelDTO = new SpotLightChannelDTO();
                        try {
                            if (childChannel.has("_id")) {
                                String idChild = childChannel.getString("_id");
                                childSpotLightChannelDTO.setId(idChild);
                            }
                        } catch (JSONException e) {
                            //spotLightChannelDTO.setDspro_id("");
                        }
                        try {
                            if (childChannel.has("dspro_id")) {
                                String dsproIdChild = childChannel.getString("dspro_id");
                                childSpotLightChannelDTO.setDspro_id(dsproIdChild);
                            }
                        } catch (JSONException e) {
                            //spotLightChannelDTO.setDspro_id("");
                        }
                        try {
                            if (childChannel.has("slug")) {
                                String slugChild = childChannel.getString("slug");
                                childSpotLightChannelDTO.setSlug(slugChild);
                            }
                        } catch (JSONException e) {
                            //spotLightChannelDTO.setDspro_id("");
                        }
                        try {
                            if (childChannel.has("is_product")) {
                                if (childChannel.getString("is_product") != null) {
                                    if (childChannel.getString("is_product").equals("true"))
                                        childSpotLightChannelDTO.setProduct(true);
                                    else
                                        childSpotLightChannelDTO.setProduct(false);
                                } else {
                                    childSpotLightChannelDTO.setProduct(false);
                                }
                            }
                        } catch (Exception em) {
                            em.printStackTrace();
                        }
                        if (childChannel.has("playlist")) {
                            if (childChannel.getJSONArray("playlist").length() > 0) {
                                if (childChannel.has("video") && childChannel.getJSONObject("video").has("_id"))
                                    childSpotLightChannelDTO.setId(childChannel.getJSONObject("video").getString("_id"));
                                childSpotLightChannelDTO.setCompany(childChannel.getString("company").toUpperCase());
                                try {
                                    String imageString = childChannel.getString("videos_thumb");
                                    imageString = CommonServiceUtils.replaceDotstudioproWithMyspotlightForImage(imageString);
                                    childSpotLightChannelDTO.setImage(imageString);
                                } catch (JSONException e) {
                                    childSpotLightChannelDTO.setImage("");
                                    e.printStackTrace();
                                }

                                try {
                                    String imageString = childChannel.getString("poster");
                                    imageString = CommonServiceUtils.replaceDotstudioproWithMyspotlightForImage(imageString);
                                    childSpotLightChannelDTO.setPoster(imageString);
                                } catch (JSONException e) {
                                    childSpotLightChannelDTO.setPoster("");
                                    e.printStackTrace();
                                }

                                childSpotLightChannelDTO.setTitle(childChannel.getString("title"));
                                try {
                                    String imageString = childChannel.getString("spotlight_poster");
                                    imageString = CommonServiceUtils.replaceDotstudioproWithMyspotlightForImage(imageString);
                                    childSpotLightChannelDTO.setSpotlightImage(imageString);
                                } catch (JSONException e) {
                                    childSpotLightChannelDTO.setSpotlightImage("");
                                    e.printStackTrace();
                                }
                                childSpotLightChannelDTO.setSlug(childChannel.getString("slug"));
                                try {
                                    JSONArray playlistArray = childChannel.getJSONArray("playlist");
                                    for (int j = 0; j < playlistArray.length(); j++) {
                                        if (playlistArray.getJSONObject(j).getString("_id").length() > 0) {
                                            childSpotLightChannelDTO.getPlaylist().add(playlistArray.getJSONObject(j).getString("_id"));
                                            VideoInfoDTO videoInfoDTO = new VideoInfoDTO();
                                            videoInfoDTO.setVideoID(playlistArray.getJSONObject(j).getString("_id"));
                                            videoInfoDTO.setThumb(playlistArray.getJSONObject(j).getString("thumb"));
                                            videoInfoDTO.setVideoTitle(playlistArray.getJSONObject(j).getString("title"));
                                            videoInfoDTO.setDescription(playlistArray.getJSONObject(j).getString("description"));
                                            try {
                                                JSONObject vidInfoDTOJSONObject = (JSONObject) playlistArray.get(j);
                                                if (vidInfoDTOJSONObject.has("custom_fields")) {
                                                    for (int k = 0; k < vidInfoDTOJSONObject.getJSONArray("custom_fields").length(); k++) {
                                                        CustomFieldDTO customFieldDTO = new CustomFieldDTO();
                                                        if (((JSONObject) vidInfoDTOJSONObject.getJSONArray("custom_fields").get(k)).has("field_title"))
                                                            customFieldDTO.setCustomFieldName(((JSONObject) vidInfoDTOJSONObject.getJSONArray("custom_fields").get(k)).getString("field_title"));
                                                        if (((JSONObject) vidInfoDTOJSONObject.getJSONArray("custom_fields").get(k)).has("field_value"))
                                                            customFieldDTO.setCustomFieldValue(((JSONObject) vidInfoDTOJSONObject.getJSONArray("custom_fields").get(k)).getString("field_value"));
                                                        videoInfoDTO.getCustomFieldsArrayList().add(customFieldDTO);
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            childSpotLightChannelDTO.getVideoInfoDTOList().add(videoInfoDTO);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                VideoInfoDTO videoInfoDTO = new VideoInfoDTO();
                                videoInfoDTO.setVideoID(childChannel.getString("video"));
                                try {
                                    videoInfoDTO.setChannelID(childChannel.getString("_id"));
                                } catch (Exception e) {
                                }
                                try {
                                    videoInfoDTO.setVideoTitle(childChannel.getString("title"));
                                } catch (Exception e) {
                                }
                                try {
                                    videoInfoDTO.setSeriesTitle(childChannel.getString("seriestitle"));
                                } catch (Exception e) {
                                }
                                try {
                                    videoInfoDTO.setDescription(childChannel.getString("description"));
                                } catch (Exception e) {
                                }
                                try {
                                    videoInfoDTO.setThumb(childChannel.getString("thumb"));
                                } catch (Exception e) {
                                }
                                try {
                                    if (videoInfoDTO.getThumb() == null || videoInfoDTO.getThumb().length() == 0) {
                                        try {
                                            String imageString = childChannel.getString("videos_thumb");
                                            imageString = CommonServiceUtils.replaceDotstudioproWithMyspotlightForImage(imageString);
                                            videoInfoDTO.setThumb(imageString);
                                        } catch (JSONException e) {
                                            videoInfoDTO.setThumb("");
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    videoInfoDTO.setSlug(childChannel.getString("slug"));
                                } catch (Exception e) {
                                }
                                try {
                                    JSONObject vidInfoDTOJSONObject = (JSONObject) childChannel;
                                    if (vidInfoDTOJSONObject.has("custom_fields")) {
                                        for (int k = 0; k < vidInfoDTOJSONObject.getJSONArray("custom_fields").length(); k++) {
                                            CustomFieldDTO customFieldDTO = new CustomFieldDTO();
                                            if (((JSONObject) vidInfoDTOJSONObject.getJSONArray("custom_fields").get(k)).has("field_title"))
                                                customFieldDTO.setCustomFieldName(((JSONObject) vidInfoDTOJSONObject.getJSONArray("custom_fields").get(k)).getString("field_title"));
                                            if (((JSONObject) vidInfoDTOJSONObject.getJSONArray("custom_fields").get(k)).has("field_value"))
                                                customFieldDTO.setCustomFieldValue(((JSONObject) vidInfoDTOJSONObject.getJSONArray("custom_fields").get(k)).getString("field_value"));
                                            videoInfoDTO.getCustomFieldsArrayList().add(customFieldDTO);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //VideoInfoDTO resultVideoInfoDTO = callBackFromUpdateVideoPausedPointServiceForBrowsePage(videoInfoDTO);
                                // AppController.getInstance().spotLightCategoriesDTOList.get(i).getSpotLightChannelDTOList().get(j).getSeasonsList().get(k).getVideoInfoDTOList().add(videoInfoDTO);
                                childSpotLightChannelDTO.getVideoInfoDTOList().add(videoInfoDTO);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        spotLightChannelDTO.getSeasonsList().add(childSpotLightChannelDTO);
                    }
                    //channelDTOList.add(spotLightChannelDTO);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //}
        } catch(Exception e ) {
            e.printStackTrace();
        }
        return spotLightChannelDTO;
    }
}
