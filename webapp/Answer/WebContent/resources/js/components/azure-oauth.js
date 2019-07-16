// var msalConfig = {
//     auth: {
//         clientId: "730b20e4-c009-4a44-a27e-fc8ff0672cc0",
//         authority: "https://login.microsoftonline.com/3843407e-c407-4775-ad35-b5ab96ebb930"
//     },
//     cache: {
//         cacheLocation: "localStorage",
//         storeAuthStateInCookie: true
//     }
// };


//     var graphConfig = {
//         graphMeEndpoint: "https://graph.microsoft.com/v1.0/me"
//     };

//     // create a request object for login or token request calls
//     // In scenarios with incremental consent, the request object can be further customized
//     var requestObj = {
//         scopes: ["user.read"]
//     };

//     var myMSALObj = new Msal.UserAgentApplication(msalConfig);

//     // Register Callbacks for redirect flow
//     // myMSALObj.handleRedirectCallbacks(acquireTokenRedirectCallBack, acquireTokenErrorRedirectCallBack);
//     myMSALObj.handleRedirectCallback(authRedirectCallBack);

//     function signIn() {
//         myMSALObj.loginPopup(requestObj).then(function (loginResponse) {
//             //Successful login
//             // showWelcomeMessage();
//             //Get the token to send to MS Graph
//             acquireTokenForcePopup();
//         }).catch(function (error) {
//             //Please check the console for errors
//             console.log(error);
//         });
//     }

//     function signOut() {
//         myMSALObj.logout();
//     }

//     function acquireTokenForcePopup() {
//         myMSALObj.acquireTokenPopup(requestObj).then(function (tokenResponse) {
//             bus.$emit('azure-login-performed', tokenResponse);
//             // callMSGraph(graphConfig.graphMeEndpoint, tokenResponse.accessToken, graphAPICallback);
//         }).catch(function (error) {
//             console.log(error);
//         });
//     }

//     // function acquireTokenPopupAndCallMSGraph() {
//     //     //Always start with acquireTokenSilent to obtain a token in the signed in user from cache
//     //     myMSALObj.acquireTokenSilent(requestObj).then(function (tokenResponse) {
//     //         bus.$emit('azure-login-performed', tokenResponse);
//     //         // callMSGraph(graphConfig.graphMeEndpoint, tokenResponse.accessToken, graphAPICallback);
//     //     }).catch(function (error) {
//     //         console.log(error);
//     //         // Upon acquireTokenSilent failure (due to consent or interaction or login required ONLY)
//     //         // Call acquireTokenPopup(popup window) 
//     //         if (requiresInteraction(error.errorCode)) {
//     //             myMSALObj.acquireTokenPopup(requestObj).then(function (tokenResponse) {
//     //                 // callMSGraph(graphConfig.graphMeEndpoint, tokenResponse.accessToken, graphAPICallback);
//     //             }).catch(function (error) {
//     //                 console.log(error);
//     //             });
//     //         }
//     //     });
//     // }

//     // function callMSGraph(theUrl, accessToken, callback) {
//     //     var xmlHttp = new XMLHttpRequest();
//     //     xmlHttp.onreadystatechange = function () {
//     //         if (this.readyState == 4 && this.status == 200)
//     //             callback(JSON.parse(this.responseText));
//     //     }
//     //     xmlHttp.open("GET", theUrl, true); // true for asynchronous
//     //     xmlHttp.setRequestHeader('Authorization', 'Bearer ' + accessToken);
//     //     xmlHttp.send();
//     // }

//     function graphAPICallback(data) {
//         console.log(data);
//         //call Vue methods
//         bus.$emit("azure-login-performed", data);
//         // document.getElementById("json").innerHTML = JSON.stringify(data, null, 2);
//     }

//    //This function can be removed if you do not need to support IE
//    function acquireTokenRedirectAndCallMSGraph() {
//         //Always start with acquireTokenSilent to obtain a token in the signed in user from cache
//         myMSALObj.acquireTokenSilent(requestObj).then(function (tokenResponse) {
//             // callMSGraph(graphConfig.graphMeEndpoint, tokenResponse.accessToken, graphAPICallback);
//         }).catch(function (error) {
//             console.log(error);
//             // Upon acquireTokenSilent failure (due to consent or interaction or login required ONLY)
//             // Call acquireTokenRedirect
//             if (requiresInteraction(error.errorCode)) {
//                 myMSALObj.acquireTokenRedirect(requestObj);
//             }
//         });
//     }

//     function authRedirectCallBack(error, response) {
//         if (error) {
//             console.log(error);
//         } else {
//             if (response.tokenType === "access_token") {
//                 // callMSGraph(graphConfig.graphMeEndpoint, response.accessToken, graphAPICallback);
//             } else {
//                 console.log("token type is:" + response.tokenType);
//             }
//         }
//     }

//     // function requiresInteraction(errorCode) {
//     //     if (!errorCode || !errorCode.length) {
//     //         return false;
//     //     }
//     //     return errorCode === "consent_required" ||
//     //         errorCode === "interaction_required" ||
//     //         errorCode === "login_required";
//     // }

