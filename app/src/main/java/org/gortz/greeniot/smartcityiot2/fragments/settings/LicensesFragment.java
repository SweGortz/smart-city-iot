package org.gortz.greeniot.smartcityiot2.fragments.settings;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.gortz.greeniot.smartcityiot2.R;
import org.gortz.greeniot.smartcityiot2.activity.SettingsActivity;
import org.gortz.greeniot.smartcityiot2.dto.listitems.License;
import org.gortz.greeniot.smartcityiot2.view.NonScrollListView;

import java.util.ArrayList;

/**
 * Display of all licenses
 */
public class LicensesFragment extends Fragment {
    SettingsActivity activity;
    NonScrollListView licenseList;
    ArrayList<License> licenses = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.license_view, container, false);
        activity = (SettingsActivity) getActivity();
        activity.hideDrawer();

        loadLicenses();

        licenseList = (NonScrollListView)v.findViewById(R.id.licenses);
        licenseList.setAdapter(new LicensesFragment.LicenseListviewContactAdapter(getActivity(), licenses));

        return v;
    }

    /**
     * Handle license list
     */
    private final class LicenseListviewContactAdapter extends BaseAdapter {
        private ArrayList<License> licenses;

        private LayoutInflater mInflater;

        public LicenseListviewContactAdapter(Context photosFragment, ArrayList<License> results){
            licenses = results;
            mInflater = LayoutInflater.from(photosFragment);
        }

        /**
         * Get current size of list
         * @return size of list
         */
        @Override
        public int getCount() {
            return licenses.size();
        }

        /**
         * Get item at position in list
         * @param position of item
         * @return item at position
         */
        @Override
        public Object getItem(int position) {
            return licenses.get(position);
        }

        /**
         * Get item id at position
         * @param position of item
         * @return id of item
         */
        @Override
        public long getItemId(int position) {
            return licenses.get(position).getId();
        }

        /**
         * Check if all items are enabled
         * @return true if all items are enabled
         */
        @Override public boolean areAllItemsEnabled() {
            return true;
        }

        /**
         * Check if item at position is enable
         * @param position of item
         * @return true if item at location is enabled
         */
        @Override public boolean isEnabled(int position) {
            return true;
        }

        /**
         * Create view for list items
         * @param position of item
         * @param convertView of item
         * @param parent of item
         * @return view of current list item
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            LicensesFragment.LicenseListviewContactAdapter.ViewHolder holder;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.license_item, null);

                holder = new LicensesFragment.LicenseListviewContactAdapter.ViewHolder();
                holder.licenseName = (TextView) convertView.findViewById(R.id.license_name);
                holder.licenseWebsite = (TextView) convertView.findViewById(R.id.license_website);
                holder.licenseContent = (TextView) convertView.findViewById(R.id.license_content);
                convertView.setTag(holder);

            } else {
                holder = (LicensesFragment.LicenseListviewContactAdapter.ViewHolder) convertView.getTag();
            }

            holder.licenseName.setText(licenses.get(position).getName());
            holder.licenseWebsite.setText(licenses.get(position).getWebsite());
            holder.licenseContent.setText(licenses.get(position).getLicenseText());
            return convertView;
        }

        private class ViewHolder{
            TextView licenseName,licenseWebsite,licenseContent;
        }
    }

    private void loadLicenses(){
        licenses.add(new License(0, "Application Smart city IoT", "https://github.com/SweGortz/SmartCityIoT", "Copyright 2017 Gortz" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "Licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this file except in compliance with the License. You may obtain a copy of the License at" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "http://www.apache.org/licenses/LICENSE-2.0" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License."));
        licenses.add(new License(1, "Library GraphView", "http://www.android-graphview.org/", "Copyright 2016 Jonas Gehring" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "Licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this file except in compliance with the License. You may obtain a copy of the License at" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "http://www.apache.org/licenses/LICENSE-2.0" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License."));
        licenses.add(new License(2, "Library Gson", "https://github.com/google/gson", "Copyright 2008 Google Inc." + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "Licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this file except in compliance with the License. You may obtain a copy of the License at" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "http://www.apache.org/licenses/LICENSE-2.0" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License."));
        licenses.add(new License(3, "Library Android Support library", "", "Copyright (C) 2011 The Android Open Source Project" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "Licensed under the Apache License, Version 2.0 (the \"License\");" + System.getProperty ("line.separator") + "you may not use this file except in compliance with the License." + System.getProperty ("line.separator") + "You may obtain a copy of the License at" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "http://www.apache.org/licenses/LICENSE-2.0" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + " Unless required by applicable law or agreed to in writing, software" + System.getProperty ("line.separator") + " distributed under the License is distributed on an \"AS IS\" BASIS," + System.getProperty ("line.separator") + " WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." + System.getProperty ("line.separator") + " See the License for the specific language governing permissions and" + System.getProperty ("line.separator") + " limitations under the License."));
        licenses.add(new License(4, "Library Powermock", "https://github.com/powermock", "Copyright 2007-2017 PowerMock Contributors" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "Licensed under the Apache License, Version 2.0 (the \"License\");\n" + "you may not use this file except in compliance with the License." + System.getProperty ("line.separator") + "You may obtain a copy of the License at" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "http://www.apache.org/licenses/LICENSE-2.0" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "Unless required by applicable law or agreed to in writing, software" + System.getProperty ("line.separator") + "distributed under the License is distributed on an \"AS IS\" BASIS," + System.getProperty ("line.separator") + "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." + System.getProperty ("line.separator") + "See the License for the specific language governing permissions and" + System.getProperty ("line.separator") + "limitations under the License."));
        licenses.add(new License(5, "Library lombok", "https://projectlombok.org/", "Copyright 2009-2017 The Project Lombok Authors"+ System.getProperty ("line.separator") + System.getProperty ("line.separator") +"Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: "+ System.getProperty ("line.separator") + System.getProperty ("line.separator") +" The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. "+ System.getProperty ("line.separator") + System.getProperty ("line.separator") +" THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE."));
        licenses.add(new License(6, "Library apache commons", "http://commons.apache.org/proper/commons-daemon/", "Copyright [yyyy] [name of copyright owner]" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "Licensed under the Apache License, Version 2.0 (the \"License\");" + System.getProperty ("line.separator") + "you may not use this file except in compliance with the License." + System.getProperty ("line.separator") + "You may obtain a copy of the License at" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "http://www.apache.org/licenses/LICENSE-2.0" + System.getProperty ("line.separator") + System.getProperty ("line.separator") + "Unless required by applicable law or agreed to in writing, software" + System.getProperty ("line.separator") + "distributed under the License is distributed on an \"AS IS\" BASIS," + System.getProperty ("line.separator") + "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." + System.getProperty ("line.separator") + "See the License for the specific language governing permissions and" + System.getProperty ("line.separator") + "limitations under the License."));

    }
}
