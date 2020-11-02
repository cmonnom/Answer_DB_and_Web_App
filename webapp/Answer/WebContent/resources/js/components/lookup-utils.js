function formatPubMedLinks(text) {
    let regex = /([0-9]{6,})/gm;
    let results = text.split(regex);
    let summary = [];
    for (let i = 0; i < results.length; i++) {
        let item = results[i];
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
    for (let i = 0; i < responseDatabases.length; i++) {
        let database = responseDatabases[i];
        let payload = response.data.payload.variantSummaries.summaries[database];
        variantSummaries[database].url = payload && payload.moreInfoUrl ? payload.moreInfoUrl : null;
        let notFoundMessage = "No summary found in " + database
        if ((!payload || !payload.summary) && variantSummaries[database].url) {
         notFoundMessage += " but the variant exists in their database.";
        }
        let summary = formatPubMedLinks(payload && payload.summary ? payload.summary : notFoundMessage );
        variantSummaries[database].summary = summary;
        variantSummaries[database].url2 = payload && payload.moreInfoUrl2 ? payload.moreInfoUrl2 : null;
     }
}