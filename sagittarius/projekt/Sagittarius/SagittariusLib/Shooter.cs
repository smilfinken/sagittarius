/*
 * Created by SharpDevelop.
 * User: johan
 * Date: 2008-09-23
 * Time: 14:13
 */
using System;

namespace Sagittarius.Lib
{
    /// <summary>
    /// Holds data for a single contestant
    /// </summary>
    public class Shooter
    {
        private string msFirstName;
        private string msLastName;
        private Scores moScores;
        
        private void Clear()
        {
            msFirstName = "";
            msLastName = "";
            moScores = new Scores();
        }
        
        /// <summary>
        /// Class constructor which creates an empty record
        /// </summary>
        public Shooter()
        {
            Clear();
        }

        /// <summary>
        /// Gets or sets the first name value
        /// </summary>
        public string FirstName
        {
            get
            {
                return msFirstName;
            }
            
            set
            {
                msFirstName = value;
            }
        }
        
        /// <summary>
        /// Gets or sets the last name value
        /// </summary>
        public string LastName
        {
            get
            {
                return msLastName;
            }
            
            set
            {
                msLastName = value;
            }
        }
        
        /// <summary>
        /// Adds a predefined score record to the shooter's set of scores
        /// </summary>
        /// <param name="score"></param>
        public void AddScore(Score score)
        {
            moScores.Add(score);
        }
        
        /// <summary>
        /// Updates a specified score record, replacing it with the supplied one
        /// </summary>
        /// <param name="station">Index of the record to update</param>
        /// <param name="score">Predefined record</param>
        public void UpdateScore(int station, Score score)
        {
            moScores.Update(station, score);
        }

        /// <summary>
        /// Updates a specified score record, replacing the recorded number of points
        /// </summary>
        /// <param name="station">Index of the record to update</param>
        /// <param name="points">Updated value</param>
        public void UpdateScore(int station, int points)
        {
            Score poScore = new Score(ScoreType.Target);
            
            if (moScores.Type(station) == poScore.Type)
            {
                poScore.Points = points;
                moScores.Update(station, poScore);
            }
        }
        
        /// <summary>
        /// Returns the sum total of the recorded points for this shooter
        /// </summary>
        /// <returns>Total number of points</returns>
        public int Points()
        {
            return moScores.TotalPoints;
        }
        
        /// <summary>
        /// Returns the number of points for a specific score record
        /// </summary>
        /// <param name="station">Index of the score record</param>
        /// <returns>Number of points</returns>
        public int Points(int station)
        {
            return moScores.Points(station);
        }
        
        /// <summary>
        /// Returns the sum total of the recorded hits for this shooter
        /// </summary>
        /// <returns>Total number of hits</returns>
        public int Hits()
        {
            return moScores.TotalHits;
        }
        
        /// <summary>
        /// Returns the number of hits for a specific score record
        /// </summary>
        /// <param name="station">Index of the score record</param>
        /// <returns>Number of hits</returns>
        public int Hits(int station)
        {
            return moScores.Hits(station);
        }
        
        /// <summary>
        /// Returns the sum total of the recorded targets hit for this shooter
        /// </summary>
        /// <returns>Total number of targets hit</returns>
        public int Targets()
        {
            return moScores.TotalTargets;
        }
        
        /// <summary>
        /// Returns the number of targets hit for a specific score record
        /// </summary>
        /// <param name="station">Index of the score record</param>
        /// <returns>Number of targets hit</returns>
        public int Targets(int station)
        {
            return moScores.Targets(station);
        }
    }
}