/**
 * Copyright (c) 2018-2099, DreamLu 卢春梦 (qq596392912@gmail.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springblade.core.redis.pubsub;

/**
 * 基于 Redisson 的消息发布器
 *
 * @author L.cm
 */
public interface RPubSubPublisher {

	/**
	 * 发布消息
	 *
	 * @param channel 队列名
	 * @param message 消息
	 * @return 收到消息的客户数量
	 */
	<T> Long publish(String channel, T message);

}
