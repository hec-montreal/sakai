/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2011 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package org.sakaiproject.coursemanagement.api;
/**
 *
 *An instance of an academic career.
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface AcademicCareer {
    
    /**
     * Unique id of the academic career
     * @return
     */
    public String getEid();
    public void setEid(String eId);
    
    /**
     * English description of the academic career
     * @return
     */
    public String getDescription();
    public void setDescription (String description);
    
    /**
     * French description of the academic career
     * @return
     */
    public String getDescription_fr_ca();
    public void setDescription_fr_ca (String description_fr_CA);

}
