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

package org.springblade.core.redis.serializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * redis序列化辅助类.单纯的泛型无法定义通用schema，原因是无法通过泛型T得到Class
 *
 * @author L.cm
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BytesWrapper<T> implements Cloneable {
	private T value;

	@Override
	@SuppressWarnings("unchecked")
	public BytesWrapper<T> clone() {
		try {
			return (BytesWrapper) super.clone();
		} catch (CloneNotSupportedException e) {
			return new BytesWrapper<>();
		}
	}
}
