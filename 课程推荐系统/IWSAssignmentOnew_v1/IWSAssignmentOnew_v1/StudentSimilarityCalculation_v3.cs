using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace IWSAssignmentOnew_v1
{
    public class StudentSimilarityCalculation_v3
    {
        //variables for calculating the weight for different attribute;
        public List<string> stuAttributeList = new List<string>(){"pg_ug","m_f","int_local","GPA"};
        public Dictionary<string, double> stuAttributeWeightDic = new Dictionary<string, double>();
        private Dictionary<string, double> sortedStuAttributeWeightDic = new Dictionary<string, double>();
        //store threshold value 
        public double thresHold { set; get; }

        //variables for storing student similarity result
        private Dictionary<int, double> studentsSimilarityResult = new Dictionary<int, double>();
        public Dictionary<int, double> sortedSimilarityResult = new Dictionary<int, double>();

        /**
         */
        public void calculateSimilarity(Student newStudent, Dictionary<int, Student> studentDictionary, Dictionary<int, Course> courseDictionary)
        {
            //calculate Student Attribute weight from training Data Set
            this.calculateStudentAttributeWeight(studentDictionary, courseDictionary);
            //using Linq to sort weight by ascending order
            this.sortedStuAttributeWeightDic = (from d in this.stuAttributeWeightDic
                                           orderby d.Value ascending
                                           select d).ToDictionary(pair => pair.Key, pair => pair.Value);
            //according to GPA value to set threshold
            //??????????Testing this part
            int count=0;
            if (newStudent.GPA != 0)
            {
                //sum first two values to set threshold
                foreach(string cur_Attr in this.sortedStuAttributeWeightDic.Keys)
                {
                    if(count >= 2)
                        break;
                    this.thresHold = this.thresHold + sortedStuAttributeWeightDic[cur_Attr];
                    count = count + 1;
                }
            }
            else if (newStudent.GPA == 0)
            {
                //pick up the only smallest value to set threshold
                foreach (string cur_Attr in this.sortedStuAttributeWeightDic.Keys)
                {
                    if (count > 0)
                        break;
                    this.thresHold = this.thresHold + sortedStuAttributeWeightDic[cur_Attr];
                    count = count + 1;
                }
            }

            //chech whether GPA is empty or not then give the rating score for each attribute
            double edu_rate = this.stuAttributeWeightDic["pg_ug"];
            double sex_rate = this.stuAttributeWeightDic["m_f"]; ;
            double type_rate = this.stuAttributeWeightDic["int_local"]; ;
            double GPA_rate = this.stuAttributeWeightDic["GPA"]; ;

            double currentScore = 0;
            //
            foreach (int stuID in studentDictionary.Keys)
            {
                currentScore = 0;
                //Console.WriteLine("{0}", studentDictionary[stuID]);
                //compare education attribute
                if (newStudent.education == studentDictionary[stuID].education)
                    currentScore = currentScore + edu_rate;
                //compare sex attribute
                if (newStudent.sex == studentDictionary[stuID].sex)
                    currentScore = currentScore + sex_rate;
                //compare type attribute
                if (newStudent.type == studentDictionary[stuID].type)
                    currentScore = currentScore + type_rate;
                //compare GPA attribute
                if (newStudent.GPA != 0)
                {
                    if (this.normalizeGPA(newStudent.GPA) == this.normalizeGPA(studentDictionary[stuID].GPA))
                        currentScore = currentScore + GPA_rate;
                }
                //calculate current student similarity
                studentsSimilarityResult.Add(studentDictionary[stuID].id, currentScore);
            }

            //using Linq to sort similarity by score
            this.sortedSimilarityResult = (from d in this.studentsSimilarityResult
                                           orderby d.Value descending
                                           select d).ToDictionary(pair => pair.Key, pair => pair.Value);
        }
        /**
        */
        public int normalizeGPA(int value)
        {
            if (value >= 3)
                return 1;
            else
                return 0;
        }
        /**
         * pg/ug    : 0 means postgrad, 1 means undergrad
         * m/f      : 0 means male, 1 means female
         * int/local: 0 means international, 1 means local
         * GPA      : 0 means 1,2; 1 means 3,4
         */
        public void calculateStudentAttributeWeight(Dictionary<int, Student> studentDictionary, Dictionary<int, Course> courseDictionary)
        {
            //calculate each student attribute weight
            double sum_0 = 0.0;
            int count_0 = 0;
            double sum_1 = 0.0;
            int count_1 = 0;
            List<double> tempList = null;
            double tempSum = 0.0;
            //foreach (string cur_Attr in this.stuAttributeWeightDic.Keys)
            for(int k=0;k<this.stuAttributeList.Count;k++)
            {
                //using this List to store calculation result for each record
                tempList = new List<double>();
                tempSum = 0.0;

                //loop each record in course dictionary
                foreach (int courseID in courseDictionary.Keys)
                {
                    //reset variables
                    sum_0 = 0.0;
                    count_0 = 0;
                    sum_1 = 0.0;
                    count_1 = 0;
                    for (int i = 0; i < courseDictionary[courseID].studentList.Count; i++)
                    {
                        if (courseDictionary[courseID].studentList[i] == 0)
                            continue;
                        //pg_ug attribute
                        if (this.stuAttributeList[k].Equals("pg_ug"))
                        {
                            if (studentDictionary[i + 1].education == 0)
                            {
                                sum_0 = sum_0 + courseDictionary[courseID].studentList[i];
                                count_0 = count_0 + 1;
                            }
                            else if (studentDictionary[i + 1].education == 1)
                            {
                                sum_1 = sum_1 + courseDictionary[courseID].studentList[i];
                                count_1 = count_1 + 1;
                            }
                        }
                        //m_f attribute
                        else if (this.stuAttributeList[k].Equals("m_f"))
                        {
                            if (studentDictionary[i + 1].sex == 0)
                            {
                                sum_0 = sum_0 + courseDictionary[courseID].studentList[i];
                                count_0 = count_0 + 1;
                            }
                            else if (studentDictionary[i + 1].sex == 1)
                            {
                                sum_1 = sum_1 + courseDictionary[courseID].studentList[i];
                                count_1 = count_1 + 1;
                            }
                        }
                        //int_local attribute
                        else if (this.stuAttributeList[k].Equals("int_local"))
                        {
                            if (studentDictionary[i + 1].type == 0)
                            {
                                sum_0 = sum_0 + courseDictionary[courseID].studentList[i];
                                count_0 = count_0 + 1;
                            }
                            else if (studentDictionary[i + 1].type == 1)
                            {
                                sum_1 = sum_1 + courseDictionary[courseID].studentList[i];
                                count_1 = count_1 + 1;
                            }
                        }
                        //GPA attribute
                        else if (this.stuAttributeList[k].Equals("GPA"))
                        {
                            if (normalizeGPA(studentDictionary[i + 1].GPA) == 0)
                            {
                                sum_0 = sum_0 + courseDictionary[courseID].studentList[i];
                                count_0 = count_0 + 1;
                            }
                            else if (normalizeGPA(studentDictionary[i + 1].GPA) == 1)
                            {
                                sum_1 = sum_1 + courseDictionary[courseID].studentList[i];
                                count_1 = count_1 + 1;
                            }
                        }
                    }
                    //Add absoluate difference into List
                    tempList.Add(Math.Abs(sum_0/count_0-sum_1/count_1));
                }
                //Calculate total absoluate difference
                for (int j = 0; j < tempList.Count; j++)
                    tempSum = tempSum + tempList[j];
                this.stuAttributeWeightDic.Add(this.stuAttributeList[k], tempSum / tempList.Count);
            }
        }
    }
}