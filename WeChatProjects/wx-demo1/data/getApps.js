// data/getApps.js
const { apps: mock } = require('../mock/apps')
const { get } = require('../utils/http')
const { getUseMock } = require('../utils/env')

function normalize(app) {
  return Object.assign({}, app, {
    price: Number(app.price || 0),
    rating: Number(app.rating || 0),
    downloads: Number(app.downloads || 0),
    reviews: Number(app.reviews || 0)
  })
}

// 通用比较器
function makeComparator(sortBy = 'id', order = 'asc') {
  const key = sortBy
  const dir = order === 'desc' ? -1 : 1
  return (a, b) => {
    const va = a[key]; const vb = b[key]
    if (va === vb) return (a.id - b.id) // 稳定 & 次级按 id
    return (va > vb ? 1 : -1) * dir
  }
}

/**
 * 获取应用列表
 * @param {Object} opts
 * @param {'id'|'downloads'|'rating'} opts.sortBy - 排序键
 * @param {'asc'|'desc'} opts.order - 排序方向
 * @param {string} [opts.category] - 可选分类过滤
 */
async function getApps({ sortBy = 'id', order = 'asc', category } = {}) {
  const cmp = makeComparator(sortBy, order)

  // ---- MOCK：本地过滤 + 排序 ----
  if (getUseMock()) {
    let list = mock.map(normalize)
    if (category) list = list.filter(x => x.category === category)
    return list.sort(cmp)
  }

  // ---- 后端：优先走服务端排序接口；不支持时本地兜底 ----
  let url = '/apps'

  // 已在后端实现的接口（你之前加过）
  if (!category && sortBy === 'downloads' && order === 'desc') url = '/apps/sort/downloads'
  else if (!category && sortBy === 'rating' && order === 'desc') url = '/apps/sort/rating'
  else if (category) {
    // 分类查询：若你也实现了 “/category/{category}/sort/*” 可以在此分支路由
    url = `/apps/category/${encodeURIComponent(category)}`
  }

  const list = await get(url)
  const arr = (Array.isArray(list) ? list : []).map(normalize)

  // 如果走的是通用接口（/apps 或 /apps/category/...），在前端做兜底排序
  if (
    url === '/apps' ||
    url.startsWith('/apps/category/')
  ) {
    return arr.sort(cmp)
  }
  return arr
}

module.exports = { getApps }
