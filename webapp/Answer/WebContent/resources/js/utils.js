//When creating a dialog, this method will calculate the max height
//of the dialog content that needs scrolling
//offset should be the height of the action bar at the bottom if any
//returns the style that should be applied to the dialog content
function getDialogMaxHeight(offset) {
    if (offset == null) {
        offset = 130;
    }
    let height = window.innerHeight - offset;
    return "min-height:" + height + "px;max-height:" + height + "px; overflow-y: auto";
}

function getDialogMaxHeightOuter(offset) {
    if (offset == null) {
        offset = 130;
    }
    let height = window.outerHeight - offset;
    return "min-height:" + height + "px;max-height:" + height + "px; overflow-y: hidden;";
}

function getDialogMaxHeightNumber(offset) {
    if (offset == null) {
        offset = 130;
    }
    let height = window.innerHeight - offset;
    return height;
}

//controls the visibility of the splash dialog
//ths goal is to hide the page until it's ready
var splashDialog = true;
var splashInterval;

//format chromosome name with leading 0 if needed
function formatChrom(chrom) {
    let formattedChrNb = null;
    if (chrom && (chrom.startsWith("chr") || chrom.startsWith("CHR"))) {
        let chrNb = chrom.substring(3, chrom.length);
        if (!isNaN(chrNb)) {
            formattedChrNb = "CHR" + (parseInt(chrNb) < 10 ? '0' : '') + chrNb;
        }
        else {
            formattedChrNb = chrom.toUpperCase();
        }
    }
    return formattedChrNb;
}

function sanitize(text) {
   return text.split("<br/>");
}

var start;
function setStart() {
    start = moment();
}

/**
 * After using setStart(), you can display
 * the number of seconds (decimal) passed since
 */
function displayDuration() {
    let end = moment();
    let duration = moment.duration(end.diff(start));
    console.log(duration.asSeconds());
}