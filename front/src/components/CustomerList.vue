<template>
    <div>
        <div v-if="customerList.length != 0">
            <table class="table table-bordered table-condensed">
                <thead>
                <tr class='info'>
                    <th></th>
                    <th>姓名</th>
                    <th>電話</th>
                    <th>生日</th>
                    <th>寵物</th>
                    <th>首次來店日期</th>
                    <th>上次來店日期</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for='(customer, idx) in customerList' :class='{success:idx==detail}'>
                    <td>
                        <button class='btn btn-info' @click="toggleDetail(idx)"><i class="fa fa-eye"></i>
                            {{buttonTitle}}
                        </button>
                    </td>
                    <td class='text-right'>{{customer.name}}</td>
                    <td>{{customer.phone}}</td>
                    <td class='text-right'>{{displayTime(customer.bdate)}}</td>
                    <td class='text-right' v-html="displayPet(customer)"></td>
                    <td class='text-right'>{{displayTime(customer.firstTime)}}</td>
                    <td class='text-right'>{{displayTime(customer.lastTime)}}</td>
                </tr>
                </tbody>
            </table>
            <pagination for="customerList" :records="total" :per-page="5"
                        count-text="第{from}到第{to}筆/共{count}筆|{count} 筆|1筆"></pagination>
        </div>
        <div v-else class="alert alert-info" role="alert">沒有符合的顧客</div>
        <hr>
        <div v-if='displayDetail'>
            <customer></customer>
        </div>
    </div>
</template>
<style>
</style>
<script>
    import moment from 'moment'
    import axios from 'axios'
    import {Pagination, PaginationEvent} from 'vue-pagination-2'
    import Customer from './Customer.vue'
    import {mapActions} from 'vuex'

    export default {
        props: {
            url: {
                type: String,
                required: true
            },
            param: {
                type: [Object, Array]
            },
            selectCustomer: {
                type: Boolean,
                default: false
            }
        },
        data() {
            return {
                customerList: [],
                skip: 0,
                limit: 5,
                total: 0,
                detail: -1
            }
        },
        computed: {
            displayDetail: function () {
                return this.customerList.length > 0 && this.detail >= 0 && !this.selectCustomer
            },
            buttonTitle: function () {
                if (this.detail == -1) {
                    if (!this.selectCustomer)
                        return "細節"
                    else
                        return "選擇"
                }
                else
                    return "關閉"
            }

        },
        mounted: function () {
            this.fetchCustomer(this.skip, this.limit)
            PaginationEvent.$on('vue-pagination::customerList', this.handlePageChange)
        },
        watch: {
            url: function () {
                this.fetchCustomer(this.skip, this.limit)
            },
            param: function () {
                this.fetchCustomer(this.skip, this.limit)
            }
        },

        methods: {
            ...mapActions(['assignCustomer']),
            processResp(resp) {
                const ret = resp.data
                this.customerList.splice(0, this.customerList.length)
                for (let customer of ret) {
                    this.customerList.push(customer)
                }
            },
            fetchCustomer(skip, limit) {
                let request_url = `${this.url}/${skip}/${limit}`

                if (this.param) {
                    axios.post(request_url, this.param).then(this.processResp).catch((err) => {
                        alert(err)
                    })
                } else {
                    axios.get(request_url).then(this.processResp).catch((err) => {
                        alert(err)
                    })
                }
                this.fetchCustomerCount()
            },
            fetchCustomerCount() {
                let request_url = `${this.url}/count`
                if (this.param) {
                    axios.post(request_url, this.param).then(resp => {
                        this.total = resp.data
                    }).catch((err) => {
                        alert(err)
                    })
                } else {
                    axios.get(request_url).then(resp => {
                        this.total = resp.data
                    }).catch((err) => {
                        alert(err)
                    })
                }
            },
            handlePageChange(page) {
                this.skip = (page - 1) * this.limit
                this.fetchCustomer(this.skip, this.limit)
            },

            displayTime(millis) {
                return moment(millis).format('LL')
            },
            displayPet(customer) {
                const petStr =
                    customer.petList.map(pet => {
                        const info = `${pet.name} - ${pet.breed}`
                        return info
                    }).join('<br/>')
                return petStr
            },
            displayBdate(customer) {
                if (customer.bdate)
                    return moment(customer.bdate).format('LL')
                else
                    return "-"
            },
            toggleDetail(idx) {
                if (this.detail != idx)
                    this.detail = idx
                else
                    this.detail = -1
                this.assignCustomer(this.customerList[idx])
            }
        },
        components: {
            Customer,
            Pagination
        }
    }
</script>
