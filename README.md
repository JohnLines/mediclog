Mediclog
========

Android Medical information log.

MedicLog logs key medical information - blood pressure, temperature, and weight, with an emphasis on simplicity and privacy.

It is still under development, so some features may change.

<a href="https://f-droid.org/packages/org.paladyn.mediclog/">
    <img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
    alt="Get it on F-Droid"
    height="80">
</a>
<a href="https://play.google.com/store/apps/details?id=org.paladyn.mediclog">
    <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png"
    alt="Get it on Google Play"
    height="80">
</a>


## Privacy

At present the data is stored in a comma separated file in the App's private storage.
The only access is via a Send intent, to allow you to mail it to yourself (or someone else if you wish).
Note that the privacy of the sending will depend on the app that does the sending.
I use K9 mail to my own mail server.

I hope to keep the app able to function usefully while having no privileges at all, and any which may be associated with optional functions will be explained in the Privileges page on the wiki.

At some point in the future there may be an option to save to external storage, which will require more privileges.
