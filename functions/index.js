// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

//

// Take the text parameter passed to this HTTP endpoint and insert it into the
// Realtime Database under the path /messages/:pushId/original
exports.makeBid = functions.https.onRequest((req, res) => {
  // Grab the text parameter.
  const amount = req.query.amount;
  const itemId = req.query.itemId;

  var bidsRef = admin.database().ref('/items/' + itemId + '/bids');
  bidsRef.orderByKey().limitToFirst(1)
    .on("value", function(bid) {

            if(bid !== null) {
                    console.log('max bid: ' + bid.amount);
            }

          if (bid !== null && amount <= bid.amount) {
            // Throwing an HttpsError so that the client gets the error details.
            throw new functions.https.HttpsError('invalid-argument', 'Bid should be greater than last one');
          } else {
            return bidsRef.push({amount : amount}).then(() => {
              console.log('New Bid written');
              // Returning the sanitized message to the client.
              return { text: amount };
            });
          }
    });
});
