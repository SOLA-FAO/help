/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.common.help;

import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

public class HelpUtility {

    private HelpSet helpSet = null;
    private HelpBroker helpBroker;
    private String helpTopicName;
    private String langCode = "defaultlang";

    private HelpUtility() {
    }

    private static class HelpUtilityHolder {

        private static final HelpUtility INSTANCE = new HelpUtility();
    }

    public static HelpUtility getInstance() {
        return HelpUtilityHolder.INSTANCE;
    }

    /**
     * Returns a <code>HelpSet<code/> for the construction of an ActionListener.
     *
     * @return Helpset
     */
    private HelpSet getHelpSet() {
        Locale defaultLocale = Locale.getDefault(Locale.Category.FORMAT);
        String userLanguage = defaultLocale.getLanguage();

        // Check user language folder exist
        URL url = getClass().getClassLoader().getResource("org/sola/common/help/" + userLanguage);

        if (url == null) {
            userLanguage = "defaultlang";
        }

        if (langCode.equalsIgnoreCase(userLanguage)) {
            if (helpSet != null) {
                return helpSet;
            }
        }

        langCode = userLanguage;

        String pathToHS = langCode + "/helpset.hs";
        String subpath = helpTopicName;
        String initsubpath = subpath.substring(0, 1);

        if (initsubpath.contains("1")) {
            String helpsetsubpath = subpath.substring(1, 5);
            pathToHS = langCode + "/helpset" + helpsetsubpath + ".hs";
        }

        try {
            URL hsURL = getClass().getResource(pathToHS);
            LogUtility.log("Found helpset at " + pathToHS, Level.FINE);
            helpSet = new HelpSet(null, hsURL);
            helpBroker = helpSet.createHelpBroker();
        } catch (Exception ee) {
            System.out.println("helpset: " + ee.getMessage());
            System.out.println("helpset: " + pathToHS + " not found");
            String[] params = {"" + ee.getMessage(), "" + pathToHS};
            MessageUtility.displayMessage(ClientMessage.EXCEPTION_HELPSET, params);
        }
        return helpSet;
    }

    private HelpBroker getHelpBroker() {
        getHelpSet();
        if (helpBroker == null) {
            helpBroker = getHelpSet().createHelpBroker();
        }
        return helpBroker;
    }

    public void showTopic(String contextMapID) {
        helpTopicName = contextMapID;
        if (getHelpBroker().isDisplayed()) {
            getHelpBroker().setCurrentID(contextMapID);
        } else {
            getHelpBroker().initPresentation();
            getHelpBroker().setCurrentID(contextMapID);
        }
        ((javax.help.DefaultHelpBroker) getHelpBroker()).setDisplayed(true);
    }

}
