// pages/apps/list.js
const { getApps } = require('../../data/getApps')
const { getUseMock, setUseMock } = require('../../utils/env')

Page({
  data: {
    useMock: getUseMock(),
    list: [],
    loading: false,
    error: '',
    skeleton: new Array(6).fill(0),

    // —— 排序相关（与 WXML 的 <picker> 对应）——
    sortOptions: [
      { label: '默认排序', sortBy: 'id',        order: 'asc'  },
      { label: '下载量↓', sortBy: 'downloads', order: 'desc' },
      { label: '评分↓',   sortBy: 'rating',    order: 'desc' }
    ],
    sortIndex: 0,          // 当前选中的排序项
    sortBy: 'id',          // 'id' | 'downloads' | 'rating'
    order: 'asc',          // 'asc' | 'desc'
    category: ''           // 可选：分类过滤
  },

  onLoad() { this.loadData() },

  onPullDownRefresh() {
    this.loadData().finally(() => wx.stopPullDownRefresh())
  },

  onToggleMock(e) {
    const v = !!e.detail.value
    setUseMock(v)
    this.setData({ useMock: v })
    this.loadData()
  },

  // picker 的变更事件（WXML: bindchange="onSortPicker"）
  onSortPicker(e) {
    const idx = Number(e.detail.value || 0)
    const opt = this.data.sortOptions[idx] || this.data.sortOptions[0]
    this.setData({
      sortIndex: idx,
      sortBy: opt.sortBy,
      order: opt.order
    })
    this.loadData()
  },

  // 若你还有其它触发排序的入口（按钮/菜单），可以复用此方法
  onChangeSort(e) {
    const { sortBy = 'id', order = 'asc' } = e.detail || {}
    // 同步 sortIndex，便于 picker 文案联动
    const idx = this.data.sortOptions.findIndex(o => o.sortBy === sortBy && o.order === order)
    this.setData({ sortBy, order, sortIndex: idx >= 0 ? idx : 0 })
    this.loadData()
  },

  async loadData() {
    this.setData({ loading: true, error: '' })
    try {
      const { sortBy, order, category } = this.data
      const list = await getApps({ sortBy, order, category })
      this.setData({ list })
    } catch (e) {
      this.setData({ error: e.message || '加载失败' })
    } finally {
      this.setData({ loading: false })
    }
  }
})
