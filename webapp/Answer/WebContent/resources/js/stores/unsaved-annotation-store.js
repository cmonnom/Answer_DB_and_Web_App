const annotationStoreModule = {
    namespaced: true,
    state: {
        unsavedAnnotationMap: {},
        needSaving: false,
        unsavedAnnotationValues: []
    },
    mutations: {
        //variantAndAnnotation = {id: "", formattedAnnotations: [], caseId: "ORD1234", type: "snp"}
        updateVariantAnnotationSelection(state, variantAndAnnotation) {
            var lightVariant = state.unsavedAnnotationMap[variantAndAnnotation.id["$oid"]];
            if (!lightVariant) {
                lightVariant = {};
                lightVariant["_id"] = variantAndAnnotation.id;
                lightVariant.caseId = variantAndAnnotation.caseId;
                lightVariant.type = variantAndAnnotation.type;
                lightVariant.selectionStatus = {}
                state.unsavedAnnotationMap[variantAndAnnotation.id["$oid"]] = lightVariant;
            }
            for (var i = 0; i < variantAndAnnotation.formattedAnnotations.length; i++) {
                var id = variantAndAnnotation.formattedAnnotations[i]._id["$oid"];
                var existingAnnotation = lightVariant.selectionStatus[id];
                //skip only if existingAnnotation and keepSaveState
                if (!variantAndAnnotation.keepSaveState || !existingAnnotation) {
                    lightVariant.selectionStatus[id] = {
                        id: variantAndAnnotation.formattedAnnotations[i]._id,
                        isSelected:  variantAndAnnotation.formattedAnnotations[i].isSelected
                    }
                }
                else {
                    //update formattedAnnotation selection state because it comes from the database
                    //instead of the unsaved user input
                    variantAndAnnotation.formattedAnnotations[i].isSelected =  lightVariant.selectionStatus[id].isSelected;
                }
            }

            //keep the needSaving previous state otherwise
            //when a variant details is loaded without having touched the
            //annotation selection yet
            if (!variantAndAnnotation.keepSaveState) {
                state.needSaving = true;
            }
            lightVariant.annotationIdsForReporting = 
            Object.values(lightVariant.selectionStatus).filter(s => s.isSelected).map(s=> s.id);
            state.unsavedAnnotationValues = Object.values(state.unsavedAnnotationMap);
        },
        clearAfterSaving: state => {
            state.unsavedAnnotationMap = {};
            state.needSaving = false;
            state.unsavedAnnotationValues = [];
        },
    },
    getters: {
        getAnnotationSelectionToSave: state => {
            return  state.unsavedAnnotationValues
        },
        getNeedSaving: state => {
            return state.needSaving;
        },
        getAnnotationIdsForReporting: (state, getters) => (variantId) => {
            return state.unsavedAnnotationMap[variantId].annotationIdsForReporting;
        },
    }

};