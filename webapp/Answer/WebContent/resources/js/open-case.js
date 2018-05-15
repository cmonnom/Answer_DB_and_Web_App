const OpenCase = {
    template: `<div>

    <v-snackbar :timeout="4000" :bottom="true" v-model="snackBarVisible">
        {{ snackBarMessage }}
        <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
    </v-snackbar>
    <!-- save filter set dialog -->
    <v-dialog v-model="saveFilterSetDialogVisible" max-width="500px">
        <v-card class="soft-grey-background">
            <v-toolbar dark color="primary">
                <v-toolbar-title>Save Current Filter Set
                </v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon @click="saveFilterSetDialogVisible = false" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close</span>
                </v-tooltip>
            </v-toolbar>
            <v-card-text>
                <v-layout row wrap class="pt-2 pl-2 pr-2">
                    <v-flex xs4 v-if="saveFilterSetId == -1" class="subheading mt-4">Filter Set Name:</v-flex>
                    <v-flex xs4 v-if="saveFilterSetId != -1" class="subheading mt-4">Modify Name:</v-flex>
                    <v-flex xs8>
                        <v-text-field v-model="saveFilterSetName" label="Filter Set Name">
                        </v-text-field>
                    </v-flex>
                    <!-- show active filters -->
                    <v-flex xs12>
                            <v-divider></v-divider>
                        <div class="pt-2 pb-2">
                            <v-chip v-if="isFilterUsed(filter)" label v-for="filter in filters" :key="filter.fieldName" :color="isInputNumberValid(filter) ? 'primary' : 'error'"
                                text-color="white">
                                <span v-html="getFilterChip(filter)"></span>
                                <v-tooltip bottom>
                                    <v-btn slot="activator" dark flat icon small @click="clearFilter(filter, true)">
                                        <v-icon>close</v-icon>
                                    </v-btn>
                                    <span>Clear Filter</span>
                                </v-tooltip>
                            </v-chip>
                            <div v-if="filter.isCheckBox" v-for="filter in filters" :key="filter.fieldName">
                                <v-chip v-if="isCheckBoxFilterUsed(checkBox)" label v-for="checkBox in filter.checkBoxes" :key="checkBox.name" color="primary"
                                    text-color="white">
                                    <span v-html="getFilterCheckBoxChip(filter, checkBox)"></span>
                                    <v-tooltip bottom>
                                        <v-btn slot="activator" dark flat icon small @click="clearCheckBoxFilter(checkBox)">
                                            <v-icon>close</v-icon>
                                        </v-btn>
                                        <span>Clear Filter</span>
                                    </v-tooltip>
                                </v-chip>
                            </div>
                        </div>
                        <v-divider></v-divider>
                    </v-flex>
                </v-layout>
            </v-card-text>
            <v-card-actions>
                <v-tooltip top>
                    <v-btn color="primary" :disabled="!saveFilterSetName" @click="saveCurrentFilters()" slot="activator">
                        <span v-if="saveFilterSetId == -1">Create</span>
                        <span v-if="saveFilterSetId != -1">Update</span>
                        <v-icon right dark>save</v-icon>
                    </v-btn>
                    <span>Save Filter Set</span>
                </v-tooltip>
                <v-tooltip top>
                    <v-btn color="warning" :disabled="saveFilterSetId == -1" @click="deleteFilterSet(saveFilterSetId)" slot="activator">
                        Delete
                        <v-icon right dark>delete</v-icon>
                    </v-btn>
                    <span>Delete Filter Set</span>
                </v-tooltip>
                <v-btn color="error" @click="saveFilterSetDialogVisible = false" slot="activator">Cancel
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
    <!-- save selection dialog -->
    <v-dialog v-model="saveDialogVisible" scrollable fullscreen hide-overlay transition="dialog-bottom-transition">
        <v-card class="soft-grey-background">
            <v-toolbar dark color="primary">
                <v-toolbar-title>Review Selected Variants
                </v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon @click="saveDialogVisible = false" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close</span>
                </v-tooltip>
            </v-toolbar>
            <v-card-text :style="getDialogMaxHeight()">
                <data-table ref="variantsSelected" :fixed="false" :fetch-on-created="false" table-title="Selected Variants" initial-sort="chromPos"
                    no-data-text="No Data" :show-row-count="true">
                </data-table>
            </v-card-text>
            <v-card-actions>
                <v-tooltip top>
                    <v-btn color="primary" :disabled="saveVariantDisabled" @click="saveSelection()" slot="activator">Save
                        <v-icon right dark>save</v-icon>
                    </v-btn>
                    <span>Save Variant</span>
                </v-tooltip>
                <v-btn color="error" @click="saveDialogVisible = false" slot="activator">Cancel
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
    <!-- variant details dialog -->
    <v-dialog v-model="variantDetailsVisible" scrollable fullscreen hide-overlay transition="dialog-bottom-transition">
        <v-card class="soft-grey-background">
            <v-toolbar dark color="primary">
                <v-toolbar-title>Annotations for variant: {{ currentVariant.geneName }} {{ currentVariant.notation }}
                </v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon flat :color="annotationVariantDetailsVisible ? 'amber accent-2' : ''" @click="annotationVariantDetailsVisible = !annotationVariantDetailsVisible"
                        slot="activator">
                        <v-icon>zoom_in</v-icon>
                    </v-btn>
                    <span>Show/Hide Variant Details</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <v-btn icon flat :color="annotationVariantCanonicalVisible ? 'amber accent-2' : ''" @click="annotationVariantCanonicalVisible = !annotationVariantCanonicalVisible"
                        slot="activator">
                        <v-icon>table_chart</v-icon>
                    </v-btn>
                    <span>Show/Hide Canonical VCF Annotations</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <v-btn icon flat :color="annotationVariantOtherVisible ? 'amber accent-2' : ''" @click="annotationVariantOtherVisible = !annotationVariantOtherVisible"
                        slot="activator">
                        <v-icon>table_chart</v-icon>
                    </v-btn>
                    <span>Show/Hide Other VCF Annotations</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <v-btn :disabled="!mdaAnnotationsExists()" icon flat :color="(mdaAnnotationsVisible && mdaAnnotationsExists()) ? 'amber accent-2' : ''"
                        @click="mdaAnnotationsVisible = !mdaAnnotationsVisible" slot="activator">
                        <v-icon v-if="mdaAnnotationsExists()">mdi-message-bulleted</v-icon>
                        <v-icon v-if="!mdaAnnotationsExists()">mdi-message-bulleted-off</v-icon>
                    </v-btn>
                    <span v-if="mdaAnnotationsExists()">Show/Hide MDA Annotations</span>
                    <span v-if="!mdaAnnotationsExists()">No MDA Annotations</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <v-btn :disabled="!utswAnnotationsExists()" icon flat :color="(utswAnnotationsVisible && utswAnnotationsExists()) ? 'amber accent-2' : ''"
                        @click="utswAnnotationsVisible = !utswAnnotationsVisible" slot="activator">
                        <v-icon v-if="utswAnnotationsExists()">mdi-message-bulleted</v-icon>
                        <v-icon v-if="!utswAnnotationsExists()">mdi-message-bulleted-off</v-icon>
                    </v-btn>
                    <span v-if="utswAnnotationsExists()">Show/Hide UTSW Annotations</span>
                    <span v-if="!utswAnnotationsExists()">No UTSW Annotations</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <v-btn icon @click="variantDetailsVisible = false" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close Variant</span>
                </v-tooltip>


            </v-toolbar>
            <v-card-text :style="getDialogMaxHeight()">
                <v-container grid-list-md fluid>
                    <v-layout row wrap>
                        <!-- card showing the same data as the summary row -->
                        <v-flex xs12 md12 lg9 xl7 v-show="annotationVariantDetailsVisible">
                            <v-card>
                                <v-toolbar dark color="primary">
                                    <v-toolbar-title>
                                        <v-icon color="amber accent-2">zoom_in</v-icon>
                                        Variant Details
                                    </v-toolbar-title>

                                    <v-spacer></v-spacer>
                                    <v-tooltip bottom>
                                        <v-btn icon @click="annotationVariantDetailsVisible = false" slot="activator">
                                            <v-icon>close</v-icon>
                                        </v-btn>
                                        <span>Close Details</span>
                                    </v-tooltip>

                                </v-toolbar>
                                <v-container grid-list-md fluid>
                                    <v-layout row wrap>
                                        <v-flex xs4 v-for="table in variantDataTables" :key="table.name">
                                            <v-card flat>
                                                <v-card-text>
                                                    <v-list class="dense-tiles">
                                                        <v-list-tile v-for="item in table.items" :key="item.label">
                                                            <v-list-tile-content class="pb-2">
                                                                <v-layout class="full-width">
                                                                    <v-flex xs12 class="text-xs-left grow">
                                                                        <span class="selectable">{{ item.label }}:</span>
                                                                        <span v-html="item.value" class="selectable text-xs-right grow blue-grey--text text--lighten-1"></span>
                                                                    </v-flex>
                                                                </v-layout>
                                                            </v-list-tile-content>
                                                        </v-list-tile>
                                                    </v-list>
                                                </v-card-text>
                                            </v-card>
                                        </v-flex>
                                    </v-layout>
                                    <v-layout row wrap>
                                        <v-flex xs12 v-for="table in linkTable" :key="table.name">
                                            <v-card flat>
                                                <v-card-text>
                                                    <v-list class="dense-tiles">
                                                        <v-list-tile v-for="item in table.items" :key="item.label">
                                                            <v-list-tile-content class="pb-2">
                                                                <v-layout class="full-width">
                                                                    <v-flex xs12 class="text-xs-left grow">
                                                                        <span class="selectable">{{ item.label }}:</span>
                                                                        <v-tooltip v-if="item.links && id !== null" bottom v-for="id in item.ids" :key="id">
                                                                            <v-btn @click="handleIdLink(id)" slot="activator" v-html="formatIdLinkLabel(id, item)">
                                                                            </v-btn>
                                                                            <span>Open in new tab</span>
                                                                        </v-tooltip>
                                                                    </v-flex>
                                                                </v-layout>
                                                            </v-list-tile-content>
                                                        </v-list-tile>
                                                    </v-list>
                                                </v-card-text>
                                            </v-card>
                                        </v-flex>
                                    </v-layout>
                                </v-container>
                            </v-card>
                        </v-flex>
                        <v-flex xs12>
                            <div class="pb-4 pt-2" v-show="annotationVariantCanonicalVisible">
                                <data-table ref="canonicalVariantAnnotation" :fixed="false" :fetch-on-created="false" table-title="Canonical VCF Annotations"
                                    initial-sort="geneId" no-data-text="No Data" :show-pagination="false" title-icon="table_chart">
                                </data-table>
                            </div>
                            <data-table v-show="annotationVariantOtherVisible" ref="otherVariantAnnotations" :fixed="false" :fetch-on-created="false"
                                table-title="Other VCF Annotations" initial-sort="geneId" no-data-text="No Data" :show-row-count="true"
                                title-icon="table_chart">
                            </data-table>
                        </v-flex>
                        <!-- MDA Annotation card -->
                        <v-flex xs12 v-show="mdaAnnotationsVisible && mdaAnnotationsExists()">
                            <v-card>
                                <v-toolbar dark color="primary">
                                    <v-toolbar-title>
                                        <v-icon color="amber accent-2">mdi-message-bulleted</v-icon>
                                        MD Anderson Annotations
                                    </v-toolbar-title>
                                    <v-spacer></v-spacer>
                                    <v-tooltip bottom>
                                        <v-btn icon @click="mdaAnnotationsVisible = false" slot="activator">
                                            <v-icon>close</v-icon>
                                        </v-btn>
                                        <span>Close</span>
                                    </v-tooltip>
                                </v-toolbar>
                                <v-card-text v-for="(annotationCategory, index) in mdaAnnotations.annotationCategories" :key="index">
                                    <v-card flat v-if="annotationCategory">
                                        <v-card-title class="subheading">{{ annotationCategory.title }}:</v-card-title>
                                        <v-card-text class="pl-2 pr-2">{{ annotationCategory.text }} </v-card-text>
                                    </v-card>
                                </v-card-text>
                            </v-card>

                        </v-flex>
                        <v-flex xs12 v-show="utswAnnotationsVisible && utswAnnotationsExists()">
                            <v-card>
                                <v-toolbar dark color="primary">
                                    <v-toolbar-title>
                                        <v-icon color="amber accent-2">mdi-message-bulleted</v-icon>
                                        UTSW Annotations
                                    </v-toolbar-title>
                                    <v-spacer></v-spacer>
                                    <v-tooltip bottom>
                                        <v-btn icon @click="utswAnnotationsVisible = false" slot="activator">
                                            <v-icon>close</v-icon>
                                        </v-btn>
                                        <span>Close</span>
                                    </v-tooltip>
                                </v-toolbar>
                                <v-card-text v-for="(annotation, index) in utswAnnotationsFormatted" :key="index" v-html="annotation">

                                </v-card-text>
                            </v-card>

                        </v-flex>
                    </v-layout>
                </v-container>
            </v-card-text>
            <v-card-actions>
                <v-tooltip top>
                    <v-btn color="primary" @click="startUserAnnotations()" slot="activator">Add
                        <v-icon right dark>note_add</v-icon>
                    </v-btn>
                    <span>Create/Edit Your Annotations</span>
                </v-tooltip>
                <v-btn v-if="!currentRow.isSelected" :disabled="saveDialogVisible" color="success" @click="selectVariantForReport()" slot="activator">Select Variant
                    <v-icon right dark>done</v-icon>
                </v-btn>
                <v-btn v-if="currentRow.isSelected" :disabled="saveDialogVisible" color="warning" @click="removeVariantFromReport()" slot="activator">Unselect Variant
                    <v-icon right dark>done</v-icon>
                </v-btn>
                <v-btn color="error" @click="variantDetailsVisible = false" slot="activator">Cancel
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>

    <!-- annotation dialog -->
    <v-dialog v-model="annotationDialogVisible" fullscreen transition="dialog-bottom-transition" :overlay="false" scrollable>
        <v-card ref="annotationDialog" class="soft-grey-background">
            <v-toolbar dark color="primary" class="mb-2">
                <v-toolbar-title>Your Annotations for variant: {{ currentVariant.geneName }} {{ currentVariant.notation }}</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon @click="annotationDialogVisible = false" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close Annotations</span>
                </v-tooltip>
            </v-toolbar>
            <v-card-text :style="getDialogMaxHeight()">
                <v-card v-if="userEditingAnnotations.length == 0">
                    <v-card-text>
                        Click on
                        <v-btn color="primary" @click="addCustomAnnotation()">Add
                            <v-icon right dark>playlist_add</v-icon>
                        </v-btn> to create a new annotation.
                    </v-card-text>
                </v-card>
                <v-layout color="primary" row wrap v-for="(annotation, index) in userEditingAnnotations" :key="index">
                    <v-flex xs12>
                        <v-card class="mb-3">
                            <v-card-text>
                                <v-layout row wrap>
                                    <v-flex xs12>
                                        <span v-if="annotation.createdDate">
                                            <b>Created on:</b> {{ annotation.createdDate }}</span>
                                        <span v-if="annotation.modifiedDate" class="pl-4">
                                            <b>Modified on:</b> {{ annotation.modifiedDate }}</span>
                                        <v-tooltip bottom>
                                            <v-btn slot="activator" icon flat @click="annotation.isVisible = !annotation.isVisible">
                                                <v-icon v-show="!annotation.isVisible">visibility</v-icon>
                                                <v-icon v-show="annotation.isVisible">visibility_off</v-icon>
                                            </v-btn>
                                            <span>Show/Hide Annotation</span>
                                        </v-tooltip>
                                        <v-tooltip bottom>
                                            <v-btn slot="activator" icon flat @click="annotation.markedForDeletion = !annotation.markedForDeletion">
                                                <v-icon>delete</v-icon>
                                            </v-btn>
                                            <span>Delete Annotation</span>
                                        </v-tooltip>
                                        <span class="pl-4" v-show="annotation.markedForDeletion">This annotation will be deleted on SAVE. Click CANCEL or
                                            <v-icon>delete</v-icon> to cancel.
                                        </span>
                                    </v-flex>
                                    <v-flex xs12>
                                        <v-text-field v-show="annotation.isVisible" :textarea="true" ref="editAnnotation" :value="annotation.text" class="mr-2" :disabled="annotation.markedForDeletion">
                                        </v-text-field>
                                    </v-flex>
                                </v-layout>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                </v-layout>
            </v-card-text>
            <v-card-actions>
                <v-btn color="primary" @click="addCustomAnnotation()">Add
                    <v-icon right dark>playlist_add</v-icon>
                </v-btn>
                <v-btn color="success" @click="saveAnnotations()">Save
                    <v-icon right dark>save</v-icon>
                </v-btn>
                <v-btn color="error" @click="cancelAnnotations()">Cancel
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>

    <!-- advanced filtering side drawer -->
    <div v-if="advanceFilteringVisible">
        <v-navigation-drawer app width="500" class="elevation-5">
            <v-toolbar>
                <v-tooltip class="ml-0" bottom>
                    <v-menu offset-y offset-x slot="activator" class="ml-0">
                        <v-btn slot="activator" flat icon dark>
                            <v-icon color="amber accent-2">filter_list</v-icon>
                        </v-btn>
                        <v-list>

                            <v-list-tile avatar @click="clearFilters">
                                <v-list-tile-avatar>
                                    <v-icon>mdi-filter-remove-outline</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Clear Filters</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>
                            
                            <v-list-tile avatar :disabled="filterSets.length == 0">
                                <v-list-tile-avatar>
                                    <v-icon>mdi-filter-outline</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    Load Filter Set
                                </v-list-tile-content>
                                <v-list-tile-action>
                                    <v-menu open-on-hover offset-x :close-on-content-click="true">
                                        <v-btn flat icon slot="activator">
                                            <v-icon>keyboard_arrow_right</v-icon>
                                        </v-btn>
                                        <v-list>
                                            <v-list-tile v-for="(filterSet, index) in filterSetItems" :key="index" @click="loadSelectedFilterSet(filterSet)">
                                                <v-list-tile-content class="pr-4">
                                                    {{ filterSet.name }}
                                                </v-list-tile-content>
                                                <v-list-tile-action>
                                                    <v-tooltip bottom>
                                                        <v-btn slot="activator" flat icon color="primary" @click.stop="deleteFilterSet(filterSet.value)">
                                                            <v-icon>delete</v-icon>
                                                        </v-btn>
                                                        <span>Delete Filter Set</span>
                                                    </v-tooltip>
                                                  </v-list-tile-action>
                                            </v-list-tile>
                                        </v-list>
                                    </v-menu>
                                </v-list-tile-action>
                            </v-list-tile>

                            <v-list-tile avatar @click="openSaveFiltersDialog()" :disabled="!filtersValid">
                                <v-list-tile-avatar>
                                    <v-icon>save</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Edit/Save Current Filter Set</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>

                            <v-list-tile avatar @click="filterData" :disabled="!filtersValid">
                                <v-list-tile-avatar>
                                    <v-icon>refresh</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Refresh</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>

                            <v-list-tile avatar @click="toggleFilters()">
                                <v-list-tile-avatar>
                                    <v-icon>close</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Close Filter Menu</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>


                        </v-list>
                    </v-menu>
                    <span>Filter Menu</span>
                </v-tooltip>
                <div class="title ml-0">
                    Filters
                </div>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn slot="activator" flat icon color="primary" @click="clearFilters">
                        <v-icon>mdi-filter-remove-outline</v-icon>
                    </v-btn>
                    <span>Clear Filters</span>
                </v-tooltip>

                <v-tooltip right>
                    <v-menu slot="activator" open-on-hover offset-y :close-on-content-click="true" :disabled="filterSets.length == 0">
                        <v-btn flat icon slot="activator" color="primary" :disabled="filterSets.length == 0">
                            <v-icon>mdi-filter-outline</v-icon>
                        </v-btn>
                        <v-list>
                            <v-list-tile v-for="(filterSet, index) in filterSetItems" :key="index" @click="loadSelectedFilterSet(filterSet)">
                                <v-list-tile-content class="pr-4">
                                    {{ filterSet.name }}
                                </v-list-tile-content>
                                <v-list-tile-action>
                                    <v-tooltip bottom>
                                        <v-btn slot="activator" flat icon color="primary" @click.stop="deleteFilterSet(filterSet.value)">
                                            <v-icon>delete</v-icon>
                                        </v-btn>
                                        <span>Delete Filter Set</span>
                                    </v-tooltip>
                                  </v-list-tile-action>
                            </v-list-tile>
                        </v-list>
                    </v-menu>
                    <span>Load Filter Set</span>
                </v-tooltip>

                <v-tooltip bottom>
                        <v-btn slot="activator" flat icon @click="openSaveFiltersDialog()" :disabled="!filtersValid" color="primary">
                            <v-icon>save</v-icon>
                        </v-btn>
                        <span>Edit/Save Current Filter Set</span>
                    </v-tooltip>

                <v-tooltip bottom>
                    <v-btn slot="activator" flat icon :loading="loading" :color="filterNeedsReload ? 'warning' : 'primary'" @click="filterData"
                        :disabled="!filtersValid">
                        <v-icon>refresh</v-icon>
                    </v-btn>
                    <span>Refresh</span>
                </v-tooltip>

                <v-tooltip bottom>
                    <v-btn icon @click="toggleFilters()" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close Filter Menu</span>
                </v-tooltip>
            </v-toolbar>
        </v-navigation-drawer>
        <v-navigation-drawer app width="500" class="mt-6" height="calc(100% - 64px)">
            <!-- displays which filters are active -->
            <div v-if="currentFilterSet" class="pl-2 pt-2 subheading">Current Filter Set: {{ currentFilterSet.listName }}</div>
            <div class="pt-2 pb-2">
                <v-chip v-if="isFilterUsed(filter)" label v-for="filter in filters" :key="filter.fieldName" :color="isInputNumberValid(filter) ? 'primary' : 'error'"
                    text-color="white">
                    <span v-html="getFilterChip(filter)"></span>
                    <v-tooltip bottom>
                        <v-btn slot="activator" dark flat icon small @click="clearFilter(filter, true)">
                            <v-icon>close</v-icon>
                        </v-btn>
                        <span>Clear Filter</span>
                    </v-tooltip>
                </v-chip>
                <div v-if="filter.isCheckBox" v-for="filter in filters" :key="filter.fieldName">
                    <v-chip v-if="isCheckBoxFilterUsed(checkBox)" label v-for="checkBox in filter.checkBoxes" :key="checkBox.name" color="primary"
                        text-color="white">
                        <span v-html="getFilterCheckBoxChip(filter, checkBox)"></span>
                        <v-tooltip bottom>
                            <v-btn slot="activator" dark flat icon small @click="clearCheckBoxFilter(checkBox)">
                                <v-icon>close</v-icon>
                            </v-btn>
                            <span>Clear Filter</span>
                        </v-tooltip>
                    </v-chip>
                </div>
            </div>
            <v-divider></v-divider>
            <!-- list of possible filters -->
            <v-container grid-list-md>
                <v-form v-model="filtersValid">
                    <v-layout row v-for="filter in filters" :key="filter.fieldName" class="pl-3 pr-3">

                        <v-flex xs12 v-if="filter.isSelect">
                            <v-layout>
                                <v-flex xs5 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                                <v-flex xs7>
                                    <v-select class="no-height" v-bind:items="filter.selectItems" v-model="filter.value" item-text="name" item-value="value"
                                        :label="filter.headerText" @input="updateFilterNeedsReload(true)" auto autocomplete clearable></v-select>
                                </v-flex>
                            </v-layout>
                        </v-flex>

                        <v-flex xs12 v-if="filter.isBoolean">
                            <v-layout row class="pt-3">
                                <v-flex xs6>
                                    <v-switch class="no-height" color="primary" :label="filter.headerTextTrue" v-model="filter.valueTrue" @change="updateFilterNeedsReload(true)"></v-switch>
                                </v-flex>
                                <v-flex xs6>
                                    <v-switch class="no-height" color="primary" :label="filter.headerTextFalse" v-model="filter.valueFalse" @change="updateFilterNeedsReload(true)"></v-switch>
                                </v-flex>
                            </v-layout>
                        </v-flex>

                        <v-expansion-panel expand v-if="filter.isCheckBox" class="expandable-filter elevation-0">
                            <v-expansion-panel-content :value="true">
                                <div slot="header" class="subheading pl-1">{{ filter.headerText }}</div>
                                <v-layout row wrap>
                                    <v-flex xs12 lg6 v-for="(checkBox, index) in filter.checkBoxes" :key="index">
                                        <v-tooltip bottom>
                                            <v-checkbox color="primary" slot="activator" class="no-height" :label="checkBox.name" v-model="checkBox.value" @change="updateFilterNeedsReload(true)"></v-checkbox>
                                            <span>{{ checkBox.name }}</span>
                                        </v-tooltip>
                                    </v-flex>
                                </v-layout>
                            </v-expansion-panel-content>
                        </v-expansion-panel>

                        <v-flex xs12 v-if="filter.isString">
                            <v-layout>
                                <v-flex xs5 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                                <v-flex xs7>
                                    <v-text-field class="no-height" :name="filter.fieldName" :label="filter.headerText" v-model="filter.value" @input="updateFilterNeedsReload(true)"></v-text-field>
                                </v-flex>
                            </v-layout>
                        </v-flex>

                        <v-flex xs12 v-if="filter.isNumber">
                            <v-layout row>
                                <v-flex xs4 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                                <v-flex xs4>
                                    <v-text-field class="no-height" :name="filter.fieldName + '-min'" label="Min" v-model="filter.minValue" :rules="numberRules"
                                        @input="updateFilterNeedsReload(true)"></v-text-field>
                                </v-flex>
                                <v-flex xs4>
                                    <v-text-field class="no-height" :name="filter.fieldName + '-max'" label="Max" v-model="filter.maxValue" :rules="numberRules"
                                        @input="updateFilterNeedsReload(true)"></v-text-field>
                                </v-flex>
                            </v-layout>
                        </v-flex>

                        <v-flex xs12 v-if="filter.isDate">
                            <v-layout row>
                                <v-flex xs4 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                                <v-flex xs4>
                                    <v-menu lazy :v-model="false" transition="scale-transition" offset-y full-width :nudge-right="40" max-width="290px" min-width="290px"
                                        :close-on-content-click="true">
                                        <v-text-field class="no-height" slot="activator" label="From" v-model="filter.minValue" prepend-icon="event" readonly @input="updateFilterNeedsReload(true)"></v-text-field>
                                        <v-date-picker v-model="filter.minValue" no-title scrollable>
                                            <v-spacer></v-spacer>
                                        </v-date-picker>
                                    </v-menu>
                                </v-flex>
                                <v-flex xs4>
                                    <v-menu lazy :v-model="false" transition="scale-transition" offset-y full-width :nudge-right="40" max-width="290px" min-width="290px"
                                        :close-on-content-click="true">
                                        <v-text-field class="no-height" slot="activator" label="To" v-model="filter.maxValue" prepend-icon="event" readonly @input="updateFilterNeedsReload(true)"></v-text-field>
                                        <v-date-picker v-model="filter.maxValue" no-title scrollable>
                                            <v-spacer></v-spacer>
                                        </v-date-picker>
                                    </v-menu>
                                </v-flex>
                            </v-layout>
                        </v-flex>
                    </v-layout>
                </v-form>
            </v-container>
        </v-navigation-drawer>
    </div>
    <!-- advanced filtering side drawer -->

    <v-toolbar dark color="primary" fixed app>
        <v-tooltip class="ml-0" bottom>
            <v-menu offset-y offset-x slot="activator" class="ml-0">
                <v-btn slot="activator" flat icon dark>
                    <v-icon>more_vert</v-icon>
                </v-btn>
                <v-list>
                    <v-list-tile avatar @click="patientDetailsVisible = !patientDetailsVisible" :disabled="patientTables.length == 0">
                        <v-list-tile-avatar>
                            <v-icon>assignment_ind</v-icon>
                        </v-list-tile-avatar>
                        <v-list-tile-content>
                            <v-list-tile-title>Show/Hide Patient Details</v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>

                    <v-list-tile avatar @click="openSaveDialog()">
                        <v-list-tile-avatar>
                            <v-icon>save</v-icon>
                        </v-list-tile-avatar>
                        <v-list-tile-content>
                            <v-list-tile-title>Review Variants Selected</v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>
                </v-list>
            </v-menu>
            <span>Case Menu</span>
        </v-tooltip>
        <v-toolbar-title class="white--text ml-0">
            Working on case: {{ caseName }}
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
            <v-btn :disabled="patientTables.length == 0" flat icon @click="patientDetailsVisible = !patientDetailsVisible" slot="activator"
                :color="patientDetailsVisible ? 'amber accent-2' : ''">
                <!-- <v-icon>perm_identity</v-icon> -->
                <v-icon>assignment_ind</v-icon>
            </v-btn>
            <span>Show/Hide Patient Details</span>
        </v-tooltip>
        <v-tooltip bottom>
            <v-btn flat icon @click="openSaveDialog()" slot="activator" :color="saveDialogVisible ? 'amber accent-2' : ''">
                <v-icon>save</v-icon>
            </v-btn>
            <span>Review Variants Selected</span>
        </v-tooltip>
    </v-toolbar>
    <v-progress-linear v-if="!caseName" :indeterminate="true"></v-progress-linear>

    <v-layout v-if="patientDetailsVisible">
        <v-flex xs12 md12 lg10 xl9>
            <div class="text-xs-center pb-3">
                <v-toolbar dark color="primary">
                    <!-- <v-icon>perm_identity</v-icon> -->
                    <v-icon :color="patientDetailsVisible ? 'amber accent-2' : ''">assignment_ind</v-icon>
                    <v-toolbar-title>Patient Details</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-tooltip bottom>
                        <v-btn flat icon @click="patientDetailsVisible = false" slot="activator">
                            <v-icon>close</v-icon>
                        </v-btn>
                        <span>Close Details</span>
                    </v-tooltip>
                </v-toolbar>
                <v-card>
                    <v-container grid-list-md fluid>
                        <v-layout row wrap>
                            <v-flex xs4 v-for="table in patientTables" :key="table.name">
                                <v-card flat>
                                    <v-card-text>
                                        <v-list class="dense-tiles">
                                            <v-list-tile v-for="item in table.items" :key="item.label">
                                                <v-list-tile-content class="pb-2">
                                                    <v-layout class="full-width">
                                                        <v-flex xs6 class="text-xs-left grow">
                                                            <span class="selectable">{{ item.label }}:</span>
                                                        </v-flex>
                                                        <v-flex xs6 class="text-xs-right grow blue-grey--text text--lighten-1">
                                                            <span class="selectable">{{ item.value }}</span>
                                                        </v-flex>
                                                    </v-layout>
                                                </v-list-tile-content>
                                            </v-list-tile>
                                        </v-list>
                                    </v-card-text>
                                </v-card>
                            </v-flex>
                        </v-layout>
                    </v-container>
                </v-card>
            </div>
        </v-flex>
    </v-layout>

    <data-table ref="geneVariantDetails" :fixed="false" :fetch-on-created="false" table-title="Variants" initial-sort="chromPos"
        no-data-text="No Data" :enable-selection="true" :show-row-count="true">
        <v-tooltip bottom slot="action1">
            <v-btn slot="activator" flat icon @click="toggleFilters" :color="advanceFilteringVisible ? 'amber accent-2' : 'white'">
                <v-icon>filter_list</v-icon>
            </v-btn>
            <span>Advanced Filtering</span>
        </v-tooltip>
        <v-list-tile avatar @click="toggleFilters" slot="action1MenuItem">
            <v-list-tile-avatar>
                <v-icon>filter_list</v-icon>
            </v-list-tile-avatar>
            <v-list-tile-content>
                <v-list-tile-title>Advanced Filtering</v-list-tile-title>
            </v-list-tile-content>
        </v-list-tile>
    </data-table>
</div>`, data() {  return {
            loading: true, patientTables: [],
            patientDetailsVisible: true,
            caseName: "",
            caseId: "",
            advanceFilteringVisible: false,
            filters: [],
            effects: [],
            filtersValid: true,
            numberRules: [v => !isNaN(v) || 'Only numbers'],
            filterNeedsReload: true,
            variantDetailsVisible: false,
            currentVariant: {},
            currentRow: {},
            currentVariantFlags: [],
            variantDataTables: [],
            linkTable: [],
            saveDialogVisible: false,
            annotationVariantDetailsVisible: true,
            annotationVariantCanonicalVisible: true,
            annotationVariantOtherVisible: true,
            saveVariantDisabled: true,
            annotationDialogVisible: false,
            userAnnotations: [],
            userEditingAnnotations: [],
            snackBarMessage: "",
            snackBarVisible: false,
            utswAnnotations: [],
            utswAnnotationsFormatted: [],
            mdaAnnotations: "",
            mdaAnnotationsFormatted: [],
            mdaAnnotationsVisible: true,
            utswAnnotationsVisible: true,
            currentFilterSet: "",
            filterSets: [],
            filterSetItems: [],
            saveFilterSetName: "",
            saveFilterSetId: -1,
            saveFilterSetDialogVisible: false
        }
    }, methods: {
        handleDialogs(response, callback) {
            if (response.isXss) {
                bus.$emit("xss-error",
                    [this, response.reason]);
            } else if (response.isLogin) { bus.$emit("login-needed", [this, callback]) } else if (response.success
                === false) { bus.$emit("some-error", [this, response.message]); }
        },
        getDialogMaxHeight() {
            var height = window.innerHeight
                - 120; return "min-height:" + height + "px;max-height:" + height + "px; overflow-y: auto";
        },
        getAjaxData() {
            this.loading = true;
            axios({
                method: 'post',
                url: webAppRoot + "/getCaseDetails",
                params: {
                    caseId: this.$route.params.id
                },
                data: {
                    filters:
                        this.filters
                }
            }).then(response => {
                if (response.data.isAllowed) {
                    this.patientTables = response.data.patientInfo.patientTables;
                    this.caseName = response.data.caseName;
                    this.caseId = response.data.caseId;
                    this.$refs.geneVariantDetails.manualDataFiltered(response.data);
                    this.effects = response.data.effects;
                    this.userId = response.data.userId;
                    this.populateCheckBoxes();
                    this.filterNeedsReload = false; 
                    this.addHeaderAction(response);
                }
                else {
                    this.handleDialogs(response.data, this.getAjaxData);
                }
                this.loading = false;
            }
            ).catch(error => {
                this.loading = false;
                console.log(error);
            }
            );
        },
        getVariantFilters() {
            axios.get(
                webAppRoot + "/getVariantFilters",
                {
                    params: {
                        caseId: this.$route.params.id
                    }
                })
                .then(response => {
                    if (response.data.isAllowed) {
                        this.filters = response.data.filters;
                        this.populateCheckBoxes();
                    }
                    else {
                        this.handleDialogs(response, this.getVariantFilters);
                    }
                }).catch(error => {
                    console.log(error);
                });
        },
        populateCheckBoxes() {
            for (var i = 0; i
                < this.filters.length; i++) {
                var filter = this.filters[i]; if (filter.isCheckBox) {
                    if (filter.fieldName == 'effects') {
                        if
                        (filter.checkBoxes.length > 0) { return; } filter.checkBoxes = []; for (var j = 0; j
                            < this.effects.length; j++) { filter.checkBoxes.push({ name: this.effects[j], value: false }); }
                    }
                }
            }
        },
        filterData() {
            this.getAjaxData();
        },
        clearFilters() {
            for (var i = 0; i < this.filters.length; i++) {
                var filter = this.filters[i]; this.clearFilter(filter);
            }
            this.currentFilterSet = ""; 
            this.getAjaxData();
        },
        clearFilter(filter, doRefresh) {
            filter.value = null;
            filter.minValue = null;
            filter.maxValue = null;
            filter.minDateValue = null;
            filter.maxDateValue = null;
            filter.valueTrue = false;
            filter.valueFalse = false;
            if (filter.isCheckBox) {
                for (var i = 0; i < filter.checkBoxes.length; i++) {
                    filter.checkBoxes[i].value = false;
                }
            }
            if (doRefresh) { //refresh only if closing a chip
                this.currentFilterSet = ""; 
                this.getAjaxData();
            }
        },
        clearCheckBoxFilter(checkBox) {
            checkBox.value = false; //TODO refresh
        },
        // display the content of the filter with the appropriate value min max etc 
        getFilterChip(filter) { //TODO 
            if (filter.isSelect) {
                return filter.headerText + ": <b>" + filter.value + "</b>";
            }
            if (filter.isBoolean) {
                return "Include " + filter.headerTextTrue
                    + ": <b>" + (filter.valueTrue ? 'YES ' : 'NO ')
                    + "</b><br/>" + "Include "
                    + filter.headerTextFalse + ": <b>"
                    + (filter.valueFalse ? 'YES' : 'NO') + "</b>";
            } if (filter.isString) {
                return filter.headerText + " contains <b>" + filter.value + "</b>";
            } if (filter.isNumber) { // 
                return filter.headerText + ": <b>[ "
                    + (filter.minValue != null ? filter.minValue : '') + ":"
                    + (filter.maxValue != null ? filter.maxValue : '') + " ]</b>";
            } //TODO dates and numbers
            return filter.headerText;
        },
        getFilterCheckBoxChip(filter, checkBox) {
            return filter.headerText + ": <b>" + checkBox.name
                + "</b>";
        },
        toggleFilters() {
            this.advanceFilteringVisible = !this.advanceFilteringVisible;
            if (!this.advanceFilteringVisible) {
                bus.$emit("need-layout-resize", this);
            }
        }, 
        isFilterUsed(filter) {
            if (filter.isCheckBox) {
                return false; //handled in a separate chip
            }
            if (filter.isNumber) {
                return (filter.minValue != null && filter.minValue !== "")
                    || (filter.maxValue != null && filter.maxValue !== "")
            }
            if (filter.isDate) {
                return filter.minDateValue != null || filter.maxDateValue != null;
            }
            if (filter.isBoolean) {
                return filter.valueTrue == true || filter.valueFalse == true;
            }
            return filter.value != null && filter.value !== "";
        },
        isCheckBoxFilterUsed(checkBox) { return checkBox.value == true; }, isInputNumberValid(filter) {
            if (filter.isNumber) {
                var isValid = (filter.minValue === "" || !isNaN(filter.minValue)) && (filter.maxValue === "" || !isNaN(filter.maxValue));
                if (isValid) {
                    if (filter.minValue !== "" && filter.minValue != null) {
                        filter.minValue = parseFloat(filter.minValue);
                    }
                    if (filter.maxValue !== "" && filter.maxValue != null) {
                        filter.maxValue = parseFloat(filter.maxValue);
                    }
                } return isValid;
            } return true;
        },
        addHeaderAction(response) {
            for (var i = 0; i < response.data.headers.length; i++) {
                if (response.data.headers[i].value == "chromPos") {
                    response.data.headers[i].itemAction = this.openVariant;
                    response.data.headers[i].actionIcon = "zoom_in";
                    response.data.headers[i].actionTooltip = "Variant Details";
                    break;
                }
            }
        }, getVariantDetails(item) {
            this.currentVariantFlags = item.iconFlags.iconFlags;
            this.currentRow = item;
            axios.get(webAppRoot + "/getVariantDetails", { params: { variantId: item.oid } }).then(response => {
                if (response.data.isAllowed) {
                    this.currentVariant = response.data.variantDetails;
                    this.variantDataTables = [];
                    var infoTable = {
                        name: "infoTable",
                        items: [{
                            label: "Chromosome Position", value: this.currentVariant.chrom + ":"
                                + this.currentVariant.pos
                        },
                        {
                            label: "Gene", value: this.currentVariant.geneName
                        },
                        {
                            label: "Notation", value:
                                this.currentVariant.notation
                        },
                        {
                            label: "Alt", value: this.currentVariant.alt
                        },
                        {
                            label: "Type", value: this.currentVariant.type
                        }]
                    };
                    this.variantDataTables.push(infoTable);

                    this.linkTable = [{
                        name: "linkTable", items: [
                            {
                                label: "IDs", ids:
                                    this.currentVariant.ids, cosmicPatients: this.currentVariant.cosmicPatients, links: true
                            }
                        ]
                    }];

                    var depthTable =
                        {
                            name: "depthTable",
                            items: [
                                {
                                    label: "Tumor Total Depth",
                                    value: this.currentVariant.tumorTotalDepth
                                },
                                {
                                    label:
                                        "Tumor Alt Percent",
                                    value: this.formatPercent(this.currentVariant.tumorAltFrequency)
                                },
                                {
                                    label: "Normal Total Depth",
                                    value: this.currentVariant.normalTotalDepth
                                },
                                {
                                    label: "Normal Alt Percent",
                                    value: this.formatPercent(this.currentVariant.normalAltFrequency)
                                }, {
                                    label: "RNA Total Depth",
                                    value: this.currentVariant.rnaTotalDepth
                                },
                                {
                                    label: "RNA Alt Percent",
                                    value: this.formatPercent(this.currentVariant.rnaAltFrequency)
                                }
                            ]
                        };
                    this.variantDataTables.push(depthTable);
                    var dataTable = {
                        name: "dataTable", items: [{
                            label: "Callers",
                            value: this.currentVariant.callSet.join(", ")
                        }, {
                            label: "Filters",
                            value: this.currentVariant.filters.join(", ")
                        }]
                    };
                    this.variantDataTables.push(dataTable);
                    this.$refs.canonicalVariantAnnotation.manualDataFiltered(response.data.canonicalSummary);
                    this.$refs.otherVariantAnnotations.manualDataFiltered(response.data.otherSummary);
                    this.userAnnotations = this.currentVariant.referenceVariant.utswAnnotations.filter(a => a.userId == this.userId);
                    this.utswAnnotations = this.currentVariant.referenceVariant.utswAnnotations;
                    this.mdaAnnotations = this.currentVariant.mdaAnnotation ? this.currentVariant.mdaAnnotation : "";
                    this.variantDetailsVisible = true;
                } else {
                    this.handleDialogs(response.data, this.getVariantDetails.bind(null,
                        item));
                }
            }).catch(error => {
                console.log(error);
            });
        }, updateVariantVcfAnnotationTable() {
            var items = this.currentVariant.vcfAnnotations;
            var headers = this.vcfAnnotationHeaders;
            var headerOrder = this.vcfAnnotationHeadersOrder;
            this.$refs.variantsSelected.manualDataFiltered(
                {
                    items: items,
                    headers: headers,
                    uniqueFieldId: "cdnaPosition",
                    headerOrder: headerOrder
                }
            );
        },
        openVariant(item) {
            this.getVariantDetails(item);
        },
        handleIdLink(id) {
            var link = "";
            if (id.indexOf('rs') == 0) {
                link = "https://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=" + id;
            } else if (id.indexOf('COSM') == 0) {
                link = "http://www.google.com?search?q=" + id;
            }
            else {
                link = "https://www.bing.com/search?q=" + id;
            } window.open(link, "_blank");
        },
        formatIdLinkLabel(id, item) {
            var cosmicIds = item.ids.filter(id => id.indexOf('COSM') == 0);
            var index = cosmicIds.indexOf(id);
            if (id.indexOf('rs') == 0) {
                return "&nbsp;" + id + "&nbsp;";
            }
            else if (id.indexOf('COSM') == 0) {
                return "&nbsp;" + id + " (" + item.cosmicPatients[index] + ")&nbsp;";
            }
            else {
                return "&nbsp;" + id + "&nbsp;";
            }
        },
        formatPercent(value) {
            if (value !== null && !isNaN(value)) {
                return (Math.round(parseFloat(value)
                    * 100000) / 1000) + "%";
            } return "";
        },
        formatLocalAnnotations(annotations, showUser) {
            var formatted = [];
            for
            (var i = 0; i
                < annotations.length; i++) {
                var annotation = "";
                if (showUser) {
                    annotation = "<b>" + annotations[i].user.first + " " + annotations[i].user.last
                        + "</b>: ";
                } annotation += annotations[i].text.replace(/\n/g, "<br/>") + "<br/>";
                formatted.push(annotation);
            }
            return formatted;
        },
        startUserAnnotations() {
            //first make a copy of annotations for editing 
            //this will allow to cancel without modifying the existing annotations 
            this.userEditingAnnotations = [];
            for (var i = 0; i < this.userAnnotations.length; i++) {
                this.userEditingAnnotations.push(JSON.parse(JSON.stringify(this.userAnnotations[i])));
            }
            this.annotationDialogVisible = true;
        },
        formatAnnotations() {
            this.utswAnnotationsFormatted = this.formatLocalAnnotations(this.utswAnnotations, true);
            // this.userAnnotationsFormatted = this.formatLocalAnnotations(this.userAnnotations, false);
        },
        addCustomAnnotation() {
            //TODO 
            this.userEditingAnnotations.push({
                text: "", markedForDeletion: false, isVisible: true
            });
            this.$nextTick(function () {
                this.$refs.editAnnotation[this.$refs.editAnnotation.length - 1].focus(); this.$vuetify.goTo(
                    "textarea:last-child");
            });
        }, saveAnnotations() {
            this.annotationDialogVisible = false;
            //copy edits to original annotations 
            setTimeout(function () { // 
                this.userAnnotations = this.userEditingAnnotations; // 
            }, 2000);
            var editedAnnotations = this.$refs.editAnnotation;
            for (var i = 0; i < editedAnnotations.length; i++) {
                if (!this.userAnnotations[i]) {
                    this.userAnnotations.push({
                        annotationId:
                            -1
                    });
                } this.userAnnotations[i].text = editedAnnotations[i].inputValue;
                this.userAnnotations[i].isVisible = this.userEditingAnnotations[i].isVisible;
                this.userAnnotations[i].markedForDeletion = this.userEditingAnnotations[i].markedForDeletion;
            } this.commitAnnotations();
        },
        commitAnnotations() {
            axios.get("./commitAnnotations", {
                params: { caseId: this.$route.params.id }, data: {
                    annotations:
                        this.userAnnotations,
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    this.getVariantDetails(this.currentRow);
                    this.snackBarMessage = "Annotation(s) Saved";
                    this.snackBarVisible = true;
                } else {
                    this.handleDialogs(response.data, this.commitAnnotations);
                }
            })
                .catch(error => {
                    alert(error);
                });
        },
        cancelAnnotations() {
            this.annotationDialogVisible = false; this.$nextTick(function
                () { //wait until dialog is closed 
                this.userEditingAnnotations = [];
            });
        },
        selectVariantForReport() {
            this.$refs.geneVariantDetails.addToSelection(this.currentRow);
            this.variantDetailsVisible = false;
        },
        removeVariantFromReport() {
            this.$refs.geneVariantDetails.removeFromSelection(this.currentRow);
            this.variantDetailsVisible = false;
        },
        updateSelectedVariantTable() {
            var selectedVariants = this.$refs.geneVariantDetails.items.filter(item => item.isSelected);
            this.saveVariantDisabled = selectedVariants.length == 0;
            var headers = this.$refs.geneVariantDetails.headers;
            var headerOrder = this.$refs.geneVariantDetails.headerOrder; this.$refs.variantsSelected.manualDataFiltered(
                { items: selectedVariants, headers: headers, uniqueFieldId: "chromPos", headerOrder: headerOrder });
        },
        openSaveDialog() { 
            this.updateSelectedVariantTable(); this.saveDialogVisible = true; 
        }, 
        saveSelection() { //TODO 
            var selectedVariantIds = this.$refs.geneVariantDetails.items.filter(item => item.isSelected).map(item => item.oid);
            axios({
                method:
                    'post',
                url: webAppRoot + "/saveVariantSelection",
                params: {
                    caseId: this.$route.params.id
                }, data: {
                    selectedVariantIds:
                        selectedVariantIds
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    this.saveDialogVisible = false;
                    this.snackBarMessage = "Variant Selection Saved";
                    this.snackBarVisible = true;
                    this.getAjaxData();
                }
                else {
                    this.handleDialogs(response.data, this.saveSelection);
                } this.loading =
                    false;
            }).catch(error => {
                this.loading = false;
                console.log(error);
            });
        },
        mdaAnnotationsExists() {
            return this.mdaAnnotations != '';
        },
        utswAnnotationsExists() {
            return this.utswAnnotationsFormatted.length > 0;
        },
        openSaveFiltersDialog() {
            if (this.currentFilterSet) {
                this.saveFilterSetId = this.currentFilterSet.variantFilterListId;
                this.saveFilterSetName = this.currentFilterSet.listName;
            }
            else { //this is a new filter set
                this.saveFilterSetId = -1;
                this.saveFilterSetName = "";
            }
            this.saveFilterSetDialogVisible = true;
        },
        saveCurrentFilters() {
            axios({
                method: 'post',
                url: webAppRoot + "/saveCurrentFilters",
                params: {
                    filterListId: this.saveFilterSetId,
                    filterListName: this.saveFilterSetName
                },
                data: {
                    filters:
                        this.filters
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    this.loadUserFilterSets();
                    this.currentFilterSet = response.data.savedFilterSet;
                    this.saveFilterSetDialogVisible = false;
                    this.snackBarMessage = "Filter Set Saved";
                    this.snackBarVisible = true;
                }
                else {
                    this.handleDialogs(response.data, this.saveCurrentFilters);
                }
                this.loading = false;
            }
            ).catch(error => {
                this.loading = false;
                console.log(error);
            }
            );
        },
        deleteFilterSet(filterSetId) {
            if (this.currentFilterSet && filterSetId == this.currentFilterSet.variantFilterListId) {
                this.currentFilterSet = "";
            }
            axios({
                method: 'post',
                url: webAppRoot + "/deleteFilterSet",
                params: {
                    filterSetId: filterSetId,
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    this.loadUserFilterSets();
                    this.saveFilterSetDialogVisible = false;
                    this.snackBarMessage = "Filter Set Deleted";
                    this.snackBarVisible = true;
                }
                else {
                    this.handleDialogs(response.data, this.deleteFilterSet.bind(null, filterSetId));
                }
            }
            ).catch(error => {
                console.log(error);
            }
            );
        },
        loadUserFilterSets() {
            axios.get(
                webAppRoot + "/loadUserFilterSets",
                {
                    params: {
                        
                    }
                })
                .then(response => {
                    if (response.data.isAllowed) {
                        this.filterSets = response.data.filters;
                        this.filterSetItems = response.data.items;

                    }
                    else {
                        this.handleDialogs(response, this.loadUserFilterSets);
                    }
                }).catch(error => {
                    console.log(error);
                });
        },
        // find the filterset loaded by the user and populates all the relevant form fields
        loadSelectedFilterSet(filterSet) {
            this.clearFilters();
            this.currentFilterSet = this.filterSets.filter(f => f.variantFilterListId == filterSet.value)[0];
            for (var i =0; i < this.currentFilterSet.filters.length; i++) {
                var filter = this.currentFilterSet.filters[i];
                var filterToPopulate = this.filters.filter(f => f.fieldName == filter.field)[0];
                this.populateFilter(filterToPopulate, filter);
            }
            this.getAjaxData();
        },
        populateFilter(filterToPopulate, loadedFilter) {
            var multiplier = 1; 
            if (filterToPopulate.fieldName.includes("Frequency")) {
                multiplier = 100;
            }
            filterToPopulate.value = loadedFilter.value;
            filterToPopulate.minValue =  loadedFilter.minValue * multiplier;
            filterToPopulate.maxValue =  loadedFilter.maxValue * multiplier;
            filterToPopulate.minDateValue = null;
            filterToPopulate.maxDateValue = null;
            filterToPopulate.valueTrue = loadedFilter.valueTrue;
            filterToPopulate.valueFalse = loadedFilter.valueFalse;
            if (filterToPopulate.isCheckBox) {
                for (var i = 0; i < filterToPopulate.checkBoxes.length; i++) {
                    var field = filterToPopulate.checkBoxes[i].name.replace(/ /g, "_").toLowerCase();
                    var checkboxHasValue = false;
                    for (var j = 0; j < loadedFilter.stringValues.length; j++) {
                        var currentCheckBoxValue = loadedFilter.stringValues[j].filterString;
                        if (currentCheckBoxValue == field) {
                            checkboxHasValue = true;
                        }
                    }
                    filterToPopulate.checkBoxes[i].value = checkboxHasValue;
                }
            }
        },
        updateFilterNeedsReload(needsReload) {
            this.filterNeedsReload = needsReload;
            if (needsReload) {
                this.currentFilterSet = ""; 
            }
        }
    },
    mounted: function () {
        this.getAjaxData();
        this.loadUserFilterSets();
        bus.$emit("clear-item-selected", [this]); this.getVariantFilters();
    },
    destroyed: function () {

    },
    watch: {
        '$route': 'getAjaxData',
    }

};