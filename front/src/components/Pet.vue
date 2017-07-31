<template>
    <bootstrap-modal ref="petModal" :needHeader="false" :needFooter="false" size="large">
        <div slot="title">
            {{title}}
        </div>
        <div slot="body">
            Your body here
        </div>
        <div slot="footer">
            Your footer here
        </div>
    </bootstrap-modal>

    <div class="modal inmodal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content animated fadeIn">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span
                            aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <h4 class="modal-title">{{title}}</h4>
                </div>
                <div class="modal-body">
                    <div class="alert alert-info" role="alert">
                        <strong class="text-danger">*</strong> 必須填寫
                    </div>
                    <form>
                        <div class="form-group">
                            <label class="col-lg-2 control-label"><strong class="text-danger">*</strong>名字:</label>
                            <div class="col-lg-2">
                                <input type="text" class="form-control"
                                       v-model="pet.name"></div>
                        </div>
                        <div class="form-group">
                            <label class="col-lg-2 control-label"><strong class="text-danger">*</strong>品種:</label>
                            <div class="col-lg-2">
                                <input type="text" class="form-control"
                                       v-model="pet.breed"></div>
                        </div>
                        <div class="form-group">
                            <label class="col-lg-2 control-label">顏色:</label>
                            <div class="col-lg-2">
                                <input type="text" class="form-control"
                                       v-model="pet.color"></div>
                        </div>
                        <div class="form-group">
                            <label class="col-lg-2 control-label">體重:</label>
                            <div class="col-lg-2">
                                <input type="number" class="form-control"
                                       v-model="pet.weight"></div>
                            公斤
                        </div>
                        <div class="form-group"><label class="col-lg-2 control-label">生日:</label>
                            <div class='col-lg-4'>
                                <datepicker v-model="pet.bdate" language="zh"
                                            format="yyyy-MM-dd"></datepicker>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-lg-2 control-label">習性:</label>
                            <div class="col-lg-4">
                                <textarea class="form-control" rows="5" v-model="pet.habit"></textarea>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-lg-2 control-label">常去獸醫院:</label>
                            <div class="col-lg-2">
                                <input type="text" class="form-control"
                                       v-model="pet.hospital"></div>
                        </div>
                        <div class="form-group">
                            <label class="col-lg-2 control-label">晶片植入時間:</label>
                            <div class='col-lg-4'>
                                <datepicker v-model="pet.chip" language="zh"
                                            format="yyyy-MM-dd"></datepicker>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-lg-2 control-label">接種疫苗:</label>
                            <div class="col-lg-4">
                                <textarea class="form-control" rows="3" v-model="pet.vacineList"></textarea>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-white" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" data-dismiss="modal" @click="addPet"
                            v-if='isNew' :disabled="!readyToAdd">
                        新增
                    </button>
                    <button type="button" class="btn btn-primary" data-dismiss="modal" @click="updatePet" v-else>
                        更新
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
<script>

    import Datepicker from 'vuejs-datepicker'

    export default {
        props: {
            isNew: {
                type: Boolean,
                required: true
            },
            index: {
                type: Number,
                required: true
            },
            petParam: {
                type: Object
            }
        },
        data() {
            return {
                pet: {
                    name: "",
                    breed: "",
                    records: []
                }
            }
        },
        mounted: function(){
            console.log("mounted")
            if (this.isNew)
                console.log("isNew=true")
            else {
                Object.assign(this.pet, this.petParam)

                if (this.pet.bdate)
                    this.pet.bdate = new Date(this.pet.bdate)

                if (this.pet.chip)
                    this.pet.chip = new Date(this.pet.chip)
            }

        },
        computed: {
            title: function () {
                if (this.isNew)
                    return "新增寵物"
                else
                    return "更新寵物資訊"
            },
            readyToAdd: function () {
                if (this.pet.name.length == 0)
                    return false
                else if (this.pet.breed.length == 0)
                    return false

                return true
            }
        },
        methods: {
            changeDateToMillis(pet) {
                if (pet.bdate)
                    pet.bdate = pet.bdate.getTime()

                if (pet.chip)
                    pet.chip = pet.chip.getTime()
            },
            addPet() {
                this.changeDateToMillis(this.pet)
                this.$emit('addPet', Object.assign({}, this.pet))
                this.pet
            },
            updatePet() {
                this.changeDateToMillis(this.pet)
                this.$emit('updatePet', {
                    index: this.index,
                    pet:  Object.assign({}, this.pet)
                })
            }
        },
        components: {
            Datepicker
        }
    }
</script>
