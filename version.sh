#!/bin/bash

version=$(awk -F '[= #]' '{a=1}a==1&&$1~/version:/{print $2;exit}' pubspec.yaml)

awk "{
        gsub(/public static final String SDK_VERSION =.*/, \"public static final String SDK_VERSION = \\\"$version\\\";\");
        print;
    }" android/src/main/java/io/rong/flutter/imlib/Version.java > .tmp && mv .tmp android/src/main/java/io/rong/flutter/imlib/Version.java


awk "{
        gsub(/static final String sdkVersion = .*/, \"static final String sdkVersion = \\\"$version\\\";\");
        print;
    }" lib/src/rong_im_client.dart > .tmp && mv .tmp lib/src/rong_im_client.dart