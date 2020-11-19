# Nature and Relaxing Sounds

Nature sounds are good for the human body, better sleep, and mental health. If you are not in nature you can listen to these sounds by the android app application.

The project follows [**Media app architecture**](https://developer.android.com/guide/topics/media-apps/media-apps-overview) guidelines. It uses Android [**MediaPlayer**](https://developer.android.com/guide/topics/media/mediaplayer) to play audio files saved in the assets folder. Playback is available on Android Phones as well on **Android Auto** and the new **Android Automotive OS**.

Source code is divided into three modules **common**, **app**, and **automotive**. 
- **common** module implements MediaBrowserService and Media Player
- **app** module implements Android UI
- **automotive** module contains support for Android Automotive OS
 
![Screenshots](https://raw.githubusercontent.com/jurajkusnier/relaxing-sounds/main/screenshots/app.png)