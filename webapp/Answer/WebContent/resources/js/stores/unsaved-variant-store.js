const variantStoreModule = {
    namespaced: true,
    state: {
        unsavedVariantMap: {},
        needSaving: false,
        unsavedVariantValues: []
    },
    mutations: {
        //variantAndAnnotation = {variant: {this.currentVariant}, caseId: "ORD1234", type: "snp"}
        updateVariant(state, variantAndCaseId) {
            var lightVariant = state.unsavedVariantMap[variantAndCaseId.variant._id["$oid"]];
            if (!lightVariant) {
                lightVariant = {};
                lightVariant["_id"] = variantAndCaseId.variant._id;
                lightVariant.caseId = variantAndCaseId.caseId;
                lightVariant.variantType = variantAndCaseId.type;
            }
            lightVariant["tier"] = variantAndCaseId.variant.tier;
            lightVariant["aberrationType"] = variantAndCaseId.variant.aberrationType;
            lightVariant["notation"] = variantAndCaseId.variant.notation;
            lightVariant["geneVariant"] = variantAndCaseId.variant.geneName + " " + variantAndCaseId.variant.notation;
            lightVariant["fusionName"] = variantAndCaseId.variant.fusionName;
            lightVariant["leftGene"] = variantAndCaseId.variant.leftGene;
            lightVariant["rightGene"] = variantAndCaseId.variant.rightGene;
            lightVariant["somaticStatus"] = variantAndCaseId.variant.somaticStatus;
            state.unsavedVariantMap[variantAndCaseId.variant._id["$oid"]] = lightVariant;
            state.needSaving = true;
            // console.log(state.unsavedVariantMap);
        },
        updateUnsavedVariantValues(state) {
            state.unsavedVariantValues = Object.values(state.unsavedVariantMap);
        },
        clearAfterSaving: state => {
            state.unsavedVariantMap = {};
            state.unsavedVariantValues = [];
            state.needSaving = false;
        }
    },
    getters: {
        getVariantsToSave: state => {
            return state.unsavedVariantValues;
        },
        getNeedSaving: state => {
            return state.needSaving;
        },
        getLightVariant: (state, getters) => (variantId) => {
            return state.unsavedVariantMap[variantId];
        }
    }

};