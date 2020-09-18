const variantStoreModuleState = () => ({
    currentVariantSummary: {},
    allVariantItemsMap: {},
    allVariantItems: [],
    currentVariantItems: [],
    caseOwnerId: null
})

const variantStoreModuleMutations = {
    updateAllVariantSummary(state, summary) {
        state.currentVariantSummary = summary;
        state.currentVariantItems = [];
        if (state.currentVariantSummary && state.currentVariantSummary.items) {
            for (var i = 0; i < state.currentVariantSummary.items.length; i++) {
                var id = state.currentVariantSummary.items[i].oid;
                if (state.allVariantItemsMap[id]) {
                    //should I keep data like selection state and other possible
                    //changes in the variant?
                }
                else {
                    state.allVariantItemsMap[id]  = state.currentVariantSummary.items[i];
                    state.allVariantItems.push(state.currentVariantSummary.items[i]);
                }
                state.currentVariantItems.push(state.allVariantItemsMap[id]);
            }
        }
    },
    updateSelectedVariant(state, selection) {
        var item = state.allVariantItemsMap[selection.id];
        item.isSelected = selection.selected;
        //if selected and it's a saved selection (it's an obj not a string)
        //then create a fake selection string
        if (item.isSelected && 
            (!item.selectionPerAnnotator[selection.userId] || 
                (item.selectionPerAnnotator[selection.userId] && item.selectionPerAnnotator[selection.userId].userId))
            ) {
            item["dateSince" + selection.userId] = "<span tabindex='-1' class='v-chip v-chip--disabled v-chip--label warning v-chip--small white--text'><span class='v-chip__content'><i aria-hidden='true' class='icon material-icons mdi mdi-checkbox-marked' style='font-size: 16px; vertical-align: bottom'></i><span class='pl-2'>latest</span></span></span>";
        }
        if (!selection.selected) {
            delete item.selectionPerAnnotator[selection.userId];
            delete item["dateSince" + selection.userId];
        }
    },
    updateCaseOwnerId(state, caseOwnerId) {
        state.caseOwnerId = caseOwnerId;
    },
    //update the table items with the modifid values from currentVariant
    //{oid: "", lightVariant: {}}
    syncWithVariantDetails(state, variantsToSync) {
        var oid = variantsToSync.oid;
        var variant = state.allVariantItemsMap[oid];
        variant.tier = variantsToSync.lightVariant.tier;
        variant.aberrationType = variantsToSync.lightVariant.aberrationType;
        variant.geneVariant = variantsToSync.lightVariant.geneName + " " + variantsToSync.lightVariant.notation;
        variant.fusionName = variantsToSync.lightVariant.fusionName;
        variant.leftGene = variantsToSync.lightVariant.leftGene;
        variant.rightGene = variantsToSync.lightVariant.rightGene;
    },
    resetAll(state) {
        state.currentVariantSummary = {};
        state.allVariantItemsMap = {};
        state.allVariantItems = [];
        state.currentVariantItems = [];
        state.caseOwnerId = null;
    }
}

const variantStoreModuleGetters = {
    getAllVariantItemsMap: state => {
        return state.allVariantItemsMap;
    },
    getCurrentVariantSummary: state => {
        return state.currentVariantSummary;
    },
    getCurrentVariantSummaryItems:  (state, getters) => {
        if (!state.currentVariantItems) {
            return [];
        }
        return state.currentVariantItems;
    },
    getAllVariantItems:  state => {
        return state.allVariantItems;
    },
    getSelectedVariantItems:  (state, getters) => {
        return getters.getAllVariantItems.filter(i => i.isSelected);
    },
    getSelectedVariantItemsForReviewer: (state, getters) => {
        return getters.getAllVariantItems.filter(i => i.isSelected && (i.selectionPerAnnotator[state.caseOwnerId] != null || i["dateSince" + state.caseOwnerId] != null));
    },
    getSelectedVariantIds:  (state, getters) => {
        return getters.getSelectedVariantItems.map(i => i.oid);
    }

}

const snpStoreModule = {
    namespaced: true,
    state: variantStoreModuleState, 
    mutations: variantStoreModuleMutations, 
    getters: variantStoreModuleGetters
}
const cnvStoreModule = {
    namespaced: true,
    state: variantStoreModuleState, 
    mutations: variantStoreModuleMutations, 
    getters: variantStoreModuleGetters
}
const ftlStoreModule = {
    namespaced: true,
    state: variantStoreModuleState, 
    mutations: variantStoreModuleMutations, 
    getters: variantStoreModuleGetters
}
const virStoreModule = {
    namespaced: true,
    state: variantStoreModuleState, 
    mutations: variantStoreModuleMutations, 
    getters: variantStoreModuleGetters
}