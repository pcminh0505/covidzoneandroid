RMIT University Vietnam
Student Name: Pham Cong Minh
Student ID: s3818102

Functionality
For all user (including not login):
- View (customize) marker on map + click to see more detail + find the direction (redirect to Google Map)
- Search and filter all zones

For login user
- Can create new zone(s)
- Keep track of which zone they're joining + manage the zones they lead (export the volunteer list)

For super user:
- View all zones' information and make update

System
- Firebase services: Firestore & Firebase Authentication


System note:
Make sure you setup language keyboard inside the phone/emulator with Vietnamese
Make sure the internet connection is stable
If the current location can't be navigated / map didn't fetch anything, please consider kill and reopen the app for several time (make sure your phone can connect to the Internet)

Firebase Note
Since I'm using Phone Authentication but in the development mode, in order to test my application, please send me
your application's SHA1 and SHA256 fingerprint by follow this instruction from Firebase
https://developers.google.com/android/guides/client-auth

If you want to implement the real OTP verification: Please use one of your phone numbers
If you want to test the quick login: Please use "+84 1593 57258" with the default OTP "123456"

Since Firestore query is asynchronous and depends on the network connection, the time wait for fetching data need to be delayed for the worst scenario network connection