package com.automation.helpers;

import com.automation.pages.BasePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.Point;

import java.util.ArrayList;
import java.util.List;

public class PageActions extends BasePage {

    public PageActions(AppiumDriver driver) {
        super(driver);
    }

    /**
     * This method will check if the elements with locator passed as argument are in carousel view
     *
     * @return true if it has carousel view else false
     */
    public boolean hasCarouselView(List<MobileElement> ele, String attributeWithText) throws InterruptedException {
        int maxSwipesForCard;
        boolean hasCarouselView = false;

        maxSwipesForCard = 15;

        ArrayList<String> dealDescList = new ArrayList<String>();
        String currentDealDescription = ele.get(0).getAttribute(attributeWithText);
        dealDescList.add(currentDealDescription);
        if (ele.size() > 1) {
            int counter = 0;
            boolean moreDealsLeftToSwipe = true;
            while (moreDealsLeftToSwipe) {
                while (counter < maxSwipesForCard) {
                    if (dealDescList.size() >= 4) {
                        moreDealsLeftToSwipe=false;
                        break;
                    }

                    slideTheDealCardFromRightToLeft(ele.get(1));
                    Thread.sleep(500);
                    currentDealDescription = ele.get(0).getAttribute(attributeWithText);
                    if (!dealDescList.contains(currentDealDescription)) {
                        dealDescList.add(currentDealDescription);
                        break;
                    }
                    counter++;
                }

                if (counter >= maxSwipesForCard) {
                    for (int k = 0; k < ele.size(); k++) {
                        if (!dealDescList.contains(ele.get(k).getAttribute(attributeWithText))) {
                            dealDescList.add(ele.get(k).getAttribute(attributeWithText));
                        }
                    }

                    moreDealsLeftToSwipe = false;
                }
            }
        }

        if (dealDescList.size() > 0) {
            hasCarouselView = true;
        }
        return hasCarouselView;
    }

    public void slideTheDealCardFromRightToLeft(MobileElement ele) {
        Point pt = ele.getLocation();
        scroll(ele.getSize().getWidth(), pt.getY(), ele.getSize().getWidth() * (1 / 4), pt.getY());
    }

    public void slideTabFromRightToLeft(MobileElement ele) {
        Point pt = ele.getLocation();
        if (driver.getPlatformName().equalsIgnoreCase("ios")) {
            scroll(100, pt.getY(), 30, pt.getY());
        } else {
            scroll(pt.getX() + 100, pt.getY(), pt.getX() - 400, pt.getY());
        }
    }
    
    public void slideTabFromLeftToRight(MobileElement ele) {
        Point pt = ele.getLocation();
        if (driver.getPlatformName().equalsIgnoreCase("ios")) {
            scroll(30, pt.getY(), 100, pt.getY());
        } else {
            scroll(pt.getX() - 100, pt.getY(), pt.getX() + 100, pt.getY());
        }
    }
}
