package io.github.quiethappiness.wrench.util.redisson.domain.base.func;

import org.redisson.api.RSearch;

public interface IRedisSearch
{
	/**
	 * Returns API for RediSearch module
	 * @return RSearch object
	 */
	RSearch getSearch();
	
	//@Service
	// public class ProductSearchService {
	//
	//     private final RSearch search;
	//
	//     public ProductSearchService(RedissonClient redisson) {
	//         this.search = redisson.getSearch();
	//         createProductIndex();
	//     }
	//
	//     private void createProductIndex() {
	//         try {
	//             search.createIndex("products",
	//                 IndexOptions.defaults()
	//                     .on(IndexType.JSON)  // 使用JSON格式存储
	//                     .prefix("product:"),
	//                 Field.text("$.name").as("name").weight(3.0),
	//                 Field.text("$.description").as("description"),
	//                 Field.tag("$.category").as("category"),
	//                 Field.numeric("$.price").as("price"),
	//                 Field.tag("$.brand").as("brand"),
	//                 Field.tag("$.tags").as("tags"),
	//                 Field.numeric("$.stock").as("stock")
	//             );
	//         } catch (Exception e) {
	//             // 索引可能已存在
	//         }
	//     }
	//
	/**
	 * 添加商品到搜索索引
	 * @param id 商品ID
	 * @param product 商品对象
	 * @param <T> 商品对象类型
	 */
	<T> void indexProduct(String id, T product);
	//
	//     // 搜索商品
	//     public List<Product> searchProducts(String query, String category,
	//                                        Double minPrice, Double maxPrice) {
	//         StringBuilder searchQuery = new StringBuilder();
	//
	//         if (StringUtils.isNotBlank(query)) {
	//             searchQuery.append("@name:").append(query).append(" ");
	//         }
	//         if (StringUtils.isNotBlank(category)) {
	//             searchQuery.append("@category:{").append(category).append("} ");
	//         }
	//         if (minPrice != null && maxPrice != null) {
	//             searchQuery.append("@price:[").append(minPrice).append(" ").append(maxPrice).append("]");
	//         }
	//
	//         SearchResult result = search.search("products",
	//             searchQuery.toString().trim(),
	//             SearchOptions.defaults()
	//                 .limit(0, 50)  // 分页
	//                 .sortBy("price", true)  // 按价格排序
	//         );
	//
	//         return parseSearchResult(result);
	//     }
	//
	//     // 模糊搜索
	//     public List<Product> fuzzySearch(String keyword) {
	//         SearchResult result = search.search("products",
	//             "%" + keyword + "%",  // 模糊匹配
	//             SearchOptions.defaults()
	//                 .limit(0, 20)
	//         );
	//         return parseSearchResult(result);
	//     }
	//
	//     // 多字段搜索
	//     public List<Product> multiFieldSearch(String term) {
	//         SearchResult result = getSearch().search("products",
	//             "@name:" + term + " | @description:" + term + " | @brand:" + term,
	//             QueryOptions.defaults().limit(0, 20)
	//         );
	//         return parseSearchResult(result);
	//     }
	// }
	// 聚合统计：商品销售统计
	// public void productAnalytics() {
	// 	// 按类别统计商品数量
	// 	AggregationResult result = getSearch().aggregate("products",
	// 		"*",
	// 		AggregationOptions.defaults()
	// 			.groupBy(GroupBy.fieldNames("@category").reducers(
	// 				Reducer.count().as("count")))
	// 	);
	//
	// 	// 价格区间统计
	// 	AggregationResult priceStats = search.aggregate("products",
	// 		"*",
	// 		AggregationOptions.defaults()
	// 			.groupBy(GroupBy.fieldNames("@price").reducers(
	// 				Reducer.quantile("@price", 0.5).as("median_price"),
	// 				Reducer.avg("@price").as("avg_price")))
	// 	);
	// }
}
