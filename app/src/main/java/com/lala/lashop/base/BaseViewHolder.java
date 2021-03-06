/**
 * Copyright 2013 Joan Zapata
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lala.lashop.base;

import android.support.annotation.IdRes;
import android.view.View;
import android.widget.ImageView;

import com.lala.lashop.utils.GlideUtil;
import com.lala.lashop.utils.SupportMultipleScreensUtil;


/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class BaseViewHolder extends com.chad.library.adapter.base.BaseViewHolder {

    public BaseViewHolder(final View view) {
        super(view);
        SupportMultipleScreensUtil.scale(view);
    }

    public BaseViewHolder loadImage(@IdRes int viewId, String url) {
        ImageView view = getView(viewId);
        GlideUtil.loadImage(view.getContext(), url, view);
        return this;
    }

}
