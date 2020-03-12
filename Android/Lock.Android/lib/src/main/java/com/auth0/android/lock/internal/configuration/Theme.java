/*
 * Theme.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.android.lock.internal.configuration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import com.auth0.android.lock.R;

/**
 * Helper class to resolve Lock.Theme values.
 * <p>
 * Disclaimer: The classes in the internal package may change in the future. Don't use them directly.
 */
public class Theme implements Parcelable {

    private int headerTitle;
    private int headerLogo;
    private int headerColor;
    private int headerTitleColor;
    private int primaryColor;
    private int darkPrimaryColor;

    private static int headerColorColor;
    private static boolean headerColorColorFlag;
    private static int headerTitleColorColor;
    private static boolean headerTitleColorColorFlag;
    private static int primaryColorColor;
    private static boolean primaryColorColorFlag;
    private static int darkPrimaryColorColor;
    private static boolean darkPrimaryColorColorFlag;
    private static String headerTitleString;
    private static boolean headerTitleStringFlag;
    private static Drawable headerLogoDrawable;
    private static boolean headerLogoDrawableFlag;

    public Theme(int headerTitle, int headerLogo, int headerColor, int headerTitleColor, int primaryColor, int darkPrimaryColor) {
        this.headerTitle = headerTitle;
        this.headerLogo = headerLogo;
        this.headerColor = headerColor;
        this.headerTitleColor = headerTitleColor;
        this.primaryColor = primaryColor;
        this.darkPrimaryColor = darkPrimaryColor;
    }
    public Theme(String headerTitle, Drawable headerLogo, int headerColor, int headerTitleColor, int primaryColor, int darkPrimaryColor) {
        this.headerTitleString = headerTitle;
        this.headerTitleStringFlag = true;
        this.headerLogoDrawable = headerLogo;
        this.headerLogoDrawableFlag = true;
        this.headerColorColor = headerColor;
        this.headerColorColorFlag = true;
        this.headerTitleColorColor = headerTitleColor;
        this.headerTitleColorColorFlag = true;
        this.primaryColorColor = primaryColor;
        this.primaryColorColorFlag = true;
        this.darkPrimaryColorColor = darkPrimaryColor;
        this.darkPrimaryColorColorFlag = true;
    }

    @SuppressLint("ResourceType")
    private String resolveStringResource(Context context, @StringRes int res, @AttrRes int attrName) {
        if (res > 0) {
            return context.getString(res);
        }

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrName, typedValue, true);
        return context.getString(typedValue.resourceId);
    }

    @SuppressLint("ResourceType")
    @ColorInt
    private int resolveColorResource(Context context, @ColorRes int res, @AttrRes int attrName) {
        if (res > 0) {
            return ContextCompat.getColor(context, res);
        }

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrName, typedValue, true);
        return ContextCompat.getColor(context, typedValue.resourceId);
    }

    @SuppressLint("ResourceType")
    private Drawable resolveDrawableResource(Context context, @DrawableRes int res, @AttrRes int attrName) {
        if (res > 0) {
            return ContextCompat.getDrawable(context, res);
        }

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrName, typedValue, true);
        return ContextCompat.getDrawable(context, typedValue.resourceId);
    }

    public String getHeaderTitle(Context context) {
        if(headerTitleStringFlag)
            return headerTitleString;
        return resolveStringResource(context, headerTitle, R.attr.Auth0_HeaderTitle);
    }

    public Drawable getHeaderLogo(Context context) {
        if(headerLogoDrawableFlag)
            return headerLogoDrawable;
        return resolveDrawableResource(context, headerLogo, R.attr.Auth0_HeaderLogo);
    }

    @ColorInt
    public int getHeaderColor(Context context) {
        if(headerColorColorFlag)
            return headerColorColor;
        return resolveColorResource(context, headerColor, R.attr.Auth0_HeaderBackground);
    }

    @ColorInt
    public int getHeaderTitleColor(Context context) {
        if(headerTitleColorColorFlag)
            return headerTitleColorColor;
        return resolveColorResource(context, headerTitleColor, R.attr.Auth0_HeaderTitleColor);
    }

    @ColorInt
    public int getPrimaryColor(Context context) {
        if(primaryColorColorFlag)
            return primaryColorColor;
        return resolveColorResource(context, primaryColor, R.attr.Auth0_PrimaryColor);
    }

    @ColorInt
    public int getDarkPrimaryColor(Context context) {
        if(darkPrimaryColorColorFlag)
            return darkPrimaryColorColor;
        return resolveColorResource(context, darkPrimaryColor, R.attr.Auth0_DarkPrimaryColor);
    }

    int getCustomHeaderTitleRes() {
        return headerTitle;
    }

    int getCustomHeaderLogoRes() {
        return headerLogo;
    }

    int getCustomHeaderColorRes() {
        return headerColor;
    }

    int getCustomHeaderTitleColorRes() {
        return headerTitleColor;
    }

    int getCustomPrimaryColorRes() {
        return primaryColor;
    }

    int getCustomDarkPrimaryColorRes() {
        return darkPrimaryColor;
    }

    protected Theme(Parcel in) {
        headerTitle = in.readInt();
        headerLogo = in.readInt();
        headerColor = in.readInt();
        headerTitleColor = in.readInt();
        primaryColor = in.readInt();
        darkPrimaryColor = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(headerTitle);
        dest.writeInt(headerLogo);
        dest.writeInt(headerColor);
        dest.writeInt(headerTitleColor);
        dest.writeInt(primaryColor);
        dest.writeInt(darkPrimaryColor);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Theme> CREATOR = new Parcelable.Creator<Theme>() {
        @Override
        public Theme createFromParcel(Parcel in) {
            return new Theme(in);
        }

        @Override
        public Theme[] newArray(int size) {
            return new Theme[size];
        }
    };


    public static Builder newBuilder() {
        return new Theme.Builder();
    }

    public static class Builder {

        private int headerTitleRes;
        private String headerTitleString;
        private int headerLogoRes;
        private Drawable headerLogoDrawable;
        private int headerColorRes;
        private int headerColorColor;
        private int headerTitleColorRes;
        private int headerTitleColorColor;
        private int primaryColorRes;
        private int primaryColorColor;
        private int darkPrimaryColorRes;
        private int darkPrimaryColorColor;

        public Builder withHeaderTitle(@StringRes int title) {
            headerTitleRes = title;
            return this;
        }
        public Builder withHeaderTitleString(String title) {
            headerTitleString = title;
            return this;
        }

        public Builder withHeaderLogo(@DrawableRes int logo) {
            headerLogoRes = logo;
            return this;
        }
        public Builder withHeaderLogoDrawable(Drawable logo) {
            headerLogoDrawable = logo;
            return this;
        }

        public Builder withHeaderColor(@ColorRes int color) {
            headerColorRes = color;
            return this;
        }
        public Builder withHeaderColorColor(int color) {
            headerColorColor = color;
            return this;
        }

        public Builder withHeaderTitleColor(@ColorRes int color) {
            headerTitleColorRes = color;
            return this;
        }
        public Builder withHeaderTitleColorColor(int color) {
            headerTitleColorColor = color;
            return this;
        }

        public Builder withPrimaryColor(@ColorRes int primary) {
            primaryColorRes = primary;
            return this;
        }
        public Builder withPrimaryColorColor(int primary) {
            primaryColorColor = primary;
            return this;
        }

        public Builder withDarkPrimaryColor(@ColorRes int darkPrimary) {
            darkPrimaryColorRes = darkPrimary;
            return this;
        }
        public Builder withDarkPrimaryColorColor(int darkPrimary) {
            darkPrimaryColorColor = darkPrimary;
            return this;
        }

        public Theme build() {
            return new Theme(headerTitleRes, headerLogoRes, headerColorRes, headerTitleColorRes, primaryColorRes, darkPrimaryColorRes);
        }

        public Theme buildWithActualValues() {
            return new Theme(headerTitleString, headerLogoDrawable, headerColorColor, headerTitleColorColor, primaryColorColor, darkPrimaryColorColor);
        }
    }
}
