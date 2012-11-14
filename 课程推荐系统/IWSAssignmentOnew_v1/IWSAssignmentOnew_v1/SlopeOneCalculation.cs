using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Diagnostics;

namespace IWSAssignmentOnew_v1
{
    class WeightAndRating
    {
        //weight
        public int count { get; set; }
        //rating value
        public double rating { get; set; }
    }
    public class SlopeOneCalculation
    {
        //store whole training data set
        //private int[,] trainingDataArray = new int[10, 12];
        private int[,] trainingDataArray = null;
        //store temporary weight and rating for each course
        private Dictionary<int, List<WeightAndRating>> courseWeightRatingDic = new Dictionary<int, List<WeightAndRating>>();
        //private Dictionary<int, WeightAndRating> courseWeightRatingDic = new Dictionary<int, WeightAndRating>();

        ////store selected course and rating pairs
        //private Dictionary<int, int> selectedCourseRateDic = new Dictionary<int, int>();
        //store predict course and rating paris
        private Dictionary<int, double> predictCourseRatingDic = new Dictionary<int, double>();
        //store sorted predit course and rating pairs
        public Dictionary<int, double> sortedPredictCourseRatingDic = new Dictionary<int, double>();

        //using Slope One algorithm to calculate rating for other course
        public void predictRatingForOtherCourses(Dictionary<int, Course> courseDictionary, Student newStudent)
        {
            trainingDataArray = new int[10, courseDictionary.Count];
            //convert training course info into Matrix
            this.readTrainingDataSet(courseDictionary);
            //calculate rating
            this.calculateRating(newStudent);
        }

        //Read training data into trainingDataArray from courseDictionary
        private void readTrainingDataSet(Dictionary<int, Course> courseDictionary)
        {
            foreach (int courseID in courseDictionary.Keys)
            {
                for (int i = 0; i < courseDictionary[courseID].studentList.Count; i++)
                    this.trainingDataArray[i, courseID - 1] = courseDictionary[courseID].studentList[i];
            }
            Debug.WriteLine("****** Testing SlopeOneCalculation-readTrainingDataSet ************");
            for (int i = 0; i < 10; i++)
            {
                for (int j = 0; j < this.trainingDataArray.GetLength(1); j++)
                {
                    //Debug.Write("{0} ", Convert.ToString(this.trainingDataArray[i, j]));
                    Debug.Write(Convert.ToString(this.trainingDataArray[i, j]));
                }
                Debug.WriteLine("");
            }
        }

        //using Slope One algorithm to calculate rating score
        private void calculateRating(Student newStudent)
        {
            //1. using each selected course to predicate rating individually for each other course
            int num = 0;
            int factor_1 = 0;
            int factor_2 = 0;
            int sum = 0;
            WeightAndRating tWeightAndRating = null;
            foreach (int c_id in newStudent.courseRatingDictionary.Keys)
            {
                for (int i = 0; i < this.trainingDataArray.GetLength(1); i++)
                {
                    //ignore the newStudent's selected course
                    if (!newStudent.courseRatingDictionary.ContainsKey(i + 1))
                    {
                        //clear variables
                        num = 0;
                        sum = 0;
                        for (int j = 0; j < 10; j++)
                        {
                            factor_1 = this.trainingDataArray[j, i];
                            factor_2 = this.trainingDataArray[j, c_id - 1];
                            //both of the factor have to larger than 0
                            if (factor_1 == 0 || factor_2 == 0)
                                continue;
                            sum = sum + (factor_1 - factor_2);
                            //increase count by 1
                            num = num + 1;
                        }
                        tWeightAndRating = new WeightAndRating();
                        tWeightAndRating.count = num;
                        tWeightAndRating.rating = (sum * 1.0) / num + newStudent.courseRatingDictionary[c_id];

                        //check whether key exists in dictionary
                        if (!this.courseWeightRatingDic.ContainsKey(i + 1))
                        {
                            List<WeightAndRating> tList = new List<WeightAndRating>();
                            this.courseWeightRatingDic.Add(i + 1, tList);
                            this.courseWeightRatingDic[i + 1].Add(tWeightAndRating);
                        }
                        else
                        {
                            this.courseWeightRatingDic[i + 1].Add(tWeightAndRating);
                        }
                    }
                }
            }

            //2. according to the previous predictation rating score, calculate weighted rating score for each course
            int num1 = 0;
            double sum1 = 0.0;
            foreach (int c_id in this.courseWeightRatingDic.Keys)
            {
                num1 = 0;
                sum1 = 0.0;
                for (int i = 0; i < this.courseWeightRatingDic[c_id].Count; i++)
                {
                    num1 = num1 + this.courseWeightRatingDic[c_id][i].count;
                    sum1 = sum1 + this.courseWeightRatingDic[c_id][i].count * this.courseWeightRatingDic[c_id][i].rating;
                }
                //add final result 
                this.predictCourseRatingDic.Add(c_id, sum1 / num1);
            }

            //3. using Linq to sort by Rating Value
            this.sortedPredictCourseRatingDic = (from d in this.predictCourseRatingDic
                                                 orderby d.Value descending
                                                 select d).ToDictionary(pair => pair.Key, pair => pair.Value);
            Debug.WriteLine("****** Testing SlopeOneCalculation-readTrainingDataSet ************");
        }
    }
}