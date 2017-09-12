<template>
    <div class="modal inmodal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content animated fadeIn">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span>
                        <span class="sr-only">Close</span>
                    </button>
                    <h4 class="modal-title">查詢客戶</h4>
                </div>
                <div class="modal-body">
                    <query-customer :selectCustomer="true"></query-customer>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-white" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" data-dismiss="modal" :disabled="isEmptyCustomer">
                        確定
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>
<style>
body {
    background-color: #ff0000;
}
</style>
<script lang="ts">
import axios from 'axios'
import QueryCustomer from './QueryCustomer.vue'
import Datepicker from 'vuejs-datepicker'
import {
    State,
    Getter,
    Action,
    Mutation,
    namespace
} from 'vuex-class'
import { Component, Inject, Model, Prop, Vue, Watch } from 'vue-property-decorator'

@Component({
    components: {
        QueryCustomer
    }
})
export default class SelectCustomerDlg extends Vue {
    mounted() {
        this.emptyCustomer()
    }

    @Getter('isEmptyCustomer') isEmptyCustomer: boolean

    @Action('emptyCustomer') emptyCustomer: () => void
}
</script>
