const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

//
exports.makeFloating = functions.database.ref('/items/{pushId}/original')
    .onCreate((changeData, context) => {
      // Only edit data when it is first created.

      // Grab the current value of what was written to the Realtime Database.
      const original = changeData.itemDescription.after.val();
      console.log('Uppercasing', context.params.pushId, original);
      const uppercase = original + ' ___testing';
      // You must return a Promise when performing asynchronous tasks inside a Functions such as
      // writing to the Firebase Realtime Database.
      // Setting an "uppercase" sibling in the Realtime Database returns a Promise.
      return changeData.itemDescription.after.ref.parent.child('uppercase').set(uppercase);
    });

