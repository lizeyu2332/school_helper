<template>
  <div class="map-click-picker">
    <!-- 地图容器 -->
    <div id="map-click-container" style="width: 100%; height: 500px;"></div>
    
    <!-- 选择提示 -->
    <div class="map-tips" v-if="selectedLocation">
      <el-alert
        type="success"
        :closable="false"
        show-icon>
        <template slot="title">
          <div class="location-info">
            <div class="location-title">已选择位置：</div>
            <div class="location-address">{{ selectedLocation.displayAddress || selectedLocation.address || '未知地址' }}</div>
            <div class="location-detail" v-if="selectedLocation.detailAddress && selectedLocation.detailAddress !== selectedLocation.displayAddress">
              <span class="detail-label">详细地址：</span>{{ selectedLocation.detailAddress }}
            </div>
          </div>
        </template>
      </el-alert>
    </div>
    <div class="map-tips" v-else>
      <el-alert
        title="请在地图上点击选择商品所在地"
        type="info"
        :closable="false"
        show-icon>
      </el-alert>
    </div>
    
    <!-- 操作按钮 -->
    <div class="map-actions">
      <el-button type="primary" @click="handleConfirm" :disabled="!selectedLocation">
        确认选择
      </el-button>
      <el-button @click="handleClear" v-if="selectedLocation">重新选择</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MapClickPicker',
  data() {
    return {
      map: null,
      marker: null,
      selectedLocation: null,
      geocoder: null,
      amapKey: 'f4bd907075e26632db123ab93c643986'
    }
  },
  mounted() {
    this.initMap()
  },
  methods: {
    initMap() {
      // 检查是否已经加载过高德地图
      if (window.AMap) {
        this.createMap()
        return
      }

      // 动态加载高德地图 API
      const script = document.createElement('script')
      script.src = `https://webapi.amap.com/maps?v=1.4.15&key=${this.amapKey}&callback=initAMapClick`
      script.async = true
      script.defer = true
      
      window.initAMapClick = () => {
        this.createMap()
      }
      
      script.onerror = () => {
        this.$message.error('地图加载失败，请检查 API Key 是否正确')
      }
      
      document.head.appendChild(script)
    },
    createMap() {
      if (!window.AMap) {
        console.error('高德地图 API 未加载')
        return
      }

      // 福州大学旗山校区坐标
      const fzuCenter = [119.200000, 26.050000]
      
      // 创建地图
      this.map = new AMap.Map('map-click-container', {
        zoom: 15,
        center: fzuCenter,
        mapStyle: 'amap://styles/normal'
      })

      // 加载地理编码插件（用于初始化地图中心）
      AMap.plugin('AMap.Geocoder', () => {
        this.geocoder = new AMap.Geocoder({
          city: '福州市',
          radius: 1000
        })

        // 地理编码：根据地址获取坐标（用于初始化）
        this.geocoder.getLocation('福州大学旗山校区', (status, result) => {
          if (status === 'complete' && result.geocodes && result.geocodes.length > 0) {
            const location = result.geocodes[0].location
            const accurateCenter = [location.lng, location.lat]
            this.map.setCenter(accurateCenter)
            this.map.setZoom(15)
          }
        })
      })

      // 点击地图选择位置
      this.map.on('click', (e) => {
        const { lng, lat } = e.lnglat
        this.selectLocation(lng, lat)
      })
    },
    selectLocation(lng, lat) {
      // 移除旧标记
      if (this.marker) {
        this.map.remove(this.marker)
      }

      // 添加新标记
      this.marker = new AMap.Marker({
        position: [lng, lat],
        map: this.map,
        draggable: true, // 可拖拽
        title: '商品所在地'
      })

      // 标记拖拽事件
      this.marker.on('dragend', (e) => {
        const { lng, lat } = e.target.getPosition()
        this.getAddressByLocation(lng, lat)
      })

      // 获取地址
      this.getAddressByLocation(lng, lat)
    },
    getAddressByLocation(lng, lat) {
      const processAddress = (status, result) => {
        if (status === 'complete' && result.regeocode) {
          const regeocode = result.regeocode
          const addressComponent = regeocode.addressComponent || {}
          
          // 完整格式化地址
          let detailAddress = regeocode.formattedAddress || ''
          
          // 构建友好的中文地址显示（优先显示建筑物、POI、社区等）
          let displayAddress = ''
          
          // 1. 优先显示建筑物名称
          if (regeocode.building && regeocode.building.name) {
            displayAddress = regeocode.building.name
          }
          // 2. 显示POI（兴趣点）名称
          else if (regeocode.pois && regeocode.pois.length > 0 && regeocode.pois[0].name) {
            displayAddress = regeocode.pois[0].name
          }
          // 3. 显示社区/小区名称
          else if (regeocode.neighborhood && regeocode.neighborhood.name) {
            displayAddress = regeocode.neighborhood.name
          }
          // 4. 显示街道+门牌号
          else if (addressComponent.street) {
            displayAddress = addressComponent.street + (addressComponent.streetNumber || '')
          }
          // 5. 显示乡镇
          else if (addressComponent.township) {
            displayAddress = addressComponent.township
          }
          // 6. 从完整地址中提取主要部分（去掉省市区）
          else if (detailAddress) {
            // 尝试提取街道或具体位置信息
            const parts = detailAddress.split(/省|市|县|区/)
            if (parts.length > 1) {
              displayAddress = parts[parts.length - 1].trim()
            } else {
              displayAddress = detailAddress
            }
          }
          
          // 如果没有获取到友好地址，使用完整地址
          if (!displayAddress || displayAddress.trim() === '') {
            displayAddress = detailAddress || `坐标位置 (${lng.toFixed(6)}, ${lat.toFixed(6)})`
          }
          
          // 确保详细地址有值（使用完整格式化地址）
          if (!detailAddress || detailAddress.trim() === '') {
            detailAddress = displayAddress
          }
          
          // 如果友好地址就是完整地址，则详细地址设为空（避免重复显示）
          // 否则，详细地址使用完整格式化地址
          const finalDetailAddress = (detailAddress && detailAddress !== displayAddress) ? detailAddress : ''
          
          return {
            longitude: lng,
            latitude: lat,
            address: detailAddress, // 完整地址用于保存到数据库
            displayAddress: displayAddress, // 友好显示地址（建筑物名称等）
            detailAddress: finalDetailAddress, // 详细地址（完整格式化地址，如果与友好地址不同）
            building: regeocode.building?.name || '',
            neighborhood: regeocode.neighborhood?.name || '',
            district: addressComponent.district || '',
            street: addressComponent.street || ''
          }
        } else {
          return {
            longitude: lng,
            latitude: lat,
            address: `坐标位置 (${lng.toFixed(6)}, ${lat.toFixed(6)})`,
            displayAddress: `坐标位置 (${lng.toFixed(6)}, ${lat.toFixed(6)})`,
            detailAddress: ''
          }
        }
      }
      
      // 使用后端API获取地址（避免前端API Key平台限制）
      this.$http.get('/geocode/regeocode', {
        params: {
          longitude: lng,
          latitude: lat
        }
      }).then(res => {
        console.log('后端返回地址信息:', res)
        if (res.success && res.data) {
          this.selectedLocation = res.data
          console.log('处理后的位置信息:', this.selectedLocation)
        } else {
          // 如果后端API调用失败，使用坐标作为地址
          console.warn('获取地址失败，使用坐标作为地址', res.message)
          this.$message.warning(res.message || '获取地址失败，使用坐标作为地址')
          this.selectedLocation = {
            longitude: lng,
            latitude: lat,
            address: `坐标位置 (${lng.toFixed(6)}, ${lat.toFixed(6)})`,
            displayAddress: `坐标位置 (${lng.toFixed(6)}, ${lat.toFixed(6)})`,
            detailAddress: ''
          }
        }
      }).catch(err => {
        console.error('获取地址异常:', err)
        this.$message.error('获取地址失败，请稍后重试')
        this.selectedLocation = {
          longitude: lng,
          latitude: lat,
          address: `坐标位置 (${lng.toFixed(6)}, ${lat.toFixed(6)})`,
          displayAddress: `坐标位置 (${lng.toFixed(6)}, ${lat.toFixed(6)})`,
          detailAddress: ''
        }
      })
    },
    handleConfirm() {
      if (!this.selectedLocation) {
        this.$message.warning('请先在地图上选择一个位置')
        return
      }
      this.$emit('select', this.selectedLocation)
    },
    handleClear() {
      if (this.marker) {
        this.map.remove(this.marker)
        this.marker = null
      }
      this.selectedLocation = null
    }
  },
  beforeDestroy() {
    if (this.map) {
      this.map.destroy()
      this.map = null
    }
  }
}
</script>

<style scoped>
.map-click-picker {
  width: 100%;
}

#map-click-container {
  border: 1px solid #ddd;
  border-radius: 8px;
  margin-bottom: 15px;
}

.map-tips {
  margin-bottom: 15px;
}

.map-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.location-info {
  line-height: 1.6;
}

.location-title {
  font-weight: bold;
  margin-bottom: 5px;
}

.location-address {
  font-size: 15px;
  color: #67c23a;
  font-weight: 500;
  margin-bottom: 5px;
}

.location-detail {
  font-size: 13px;
  color: #666;
  margin-top: 5px;
}

.detail-label {
  color: #999;
}
</style>

