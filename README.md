# download-redirection
A xposed module used to intercept download requests toward system download manager, and resend them to third-party downloaders such as ADM (Advanced Download Manager). 

Those apps using the system downloader will be forced to use the downloader we set, **when activating and enabling the module, then adding them to applied list.**
***
Forked from [DownloadRedirect](https://github.com/paletteOvO/DownloadRedirect), this repo rewrite the module UI and adjust some functions.

Currently only developed for **xposed 89** and **android 7.1**
