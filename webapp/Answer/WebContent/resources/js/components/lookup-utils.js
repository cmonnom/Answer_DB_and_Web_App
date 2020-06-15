function formatPubMedLinks(text) {
    var regex = /([0-9]{6,})/gm;
    var results = text.split(regex);
    var summary = [];
    for (var i = 0; i < results.length; i++) {
        var item = results[i];
        if (isNaN(item)) {
            summary.push({text: item, type: "text"});
        }
        else {
            summary.push({text: item, type: "link"});
        }
    }
    return summary;
}

function processDatabaseSummaries(responseDatabases, response, variantSummaries) {
    for (var i = 0; i < responseDatabases.length; i++) {
        var database = responseDatabases[i];
        var payload = response.data.payload.variantSummaries.summaries[database];
        variantSummaries[database].url = payload && payload.moreInfoUrl ? payload.moreInfoUrl : null;
        var notFoundMessage = "No summary found in " + database
        if ((!payload || !payload.summary) && variantSummaries[database].url) {
         notFoundMessage += " but the variant exists in their database.";
        }
        var summary = formatPubMedLinks(payload && payload.summary ? payload.summary : notFoundMessage );
        variantSummaries[database].summary = summary;
        variantSummaries[database].url2 = payload && payload.moreInfoUrl2 ? payload.moreInfoUrl2 : null;
     }
}