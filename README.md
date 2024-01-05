Trickle
--------

Automatic Android Low-Power Mode

## DEPRECATED

Trickle can no longer be supported on new (14+) versions of Android. Recent changes to how the OS handles "background service" style applications is the cause of this deprecation.
First of all, Google is making it very hard for apps to receive permission to use a
[Foreground Service](https://support.google.com/googleplay/android-developer/answer/13392821?hl=en_EN). It is my firm belief that Trickle will never actually be granted FGS permission,
or even if it does it will be dropped in a future A15 update or eventually be kicked off the store by Google. This would jeopardize my entire developer account, and thus, is not
worth the risk of attempting. Trickle used to be able to run as a "Battery Optimization Exempted" service, but recent A14 changes with how they handle
[cached processes](https://developer.android.com/about/versions/14/behavior-changes-all) make even an exempted background service suspend functionality after
~10 minutes or so. This is unacceptable for a background style app like Trickle. Trickle would need to claim FGS permission to resolve this "suspended functionality" change,
but as stated above, this is both a policy death sentence, and would actually cause Trickle to use more battery than it would save overall.

tl;dr: Policy changes and Operating System changes have effectively killed Trickle - sorry. So long, again, and thanks for all the fish!


### Get Trickle

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" alt="Get it on Google Play" height="80">](https://play.google.com/store/apps/details?id=com.pyamsoft.trickle)
[<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png" alt="Get it on IzzyOnDroid" height="80">](https://apt.izzysoft.de/fdroid/index/apk/com.pyamsoft.trickle)

or get the APK from the
[Releases Section](https://github.com/pyamsoft/trickle/releases/latest).

### What is Trickle

Trickle is a simple app which enters the Android OS Low-Power mode when the screen is off,
and leaves Low-Power mode when the screen is on. If your device is already is Low-Power
mode, or it is being charged, Trickle will not do anything.


### Privacy

Trickle respects your privacy. Trickle is open source, and always will be. Trickle
will never track you, or sell or share your data. Trickle offers in-app purchases,
which you may purchase to support the developer. These purchases are never
required to use the application or any features.


### Development

Trickle is developed in the open on GitHub at:  

[https://github.com/pyamsoft/trickle](https://github.com/pyamsoft/trickle)

If you know a few things about Android programming and want to help out with
development, you can do so by creating issue tickets to squash bugs, and
proposing feature requests.

## License

Apache 2

```
Copyright 2023 pyamsoft

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
