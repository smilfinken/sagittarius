/*
 * Created by SharpDevelop.
 * User: johan
 * Date: 2008-09-24
 * Time: 09:39
 */
using System;
using System.Collections.Generic;

namespace Sagittarius.Lib
{
    /// <summary>
    /// Holds a complete set of scores for one match
    /// </summary>
    public class Scores
    {
        private List<Score> moScores;
        
        private void Clear()
        {
            moScores = new List<Score>();
        }
        
        /// <summary>
        /// Class constructor which generates an empty set of scores
        /// </summary>
        public Scores()
        {
            Clear();
        }
        
        /// <summary>
        /// Adds a record for a Target Shooting score
        /// </summary>
        /// <param name="points">Recorded number of points to add</param>
        public void Add(int points)
        {
            Score poScore = new Score(ScoreType.Target);
            
            poScore.Points = points;
            moScores.Add(poScore);
        }
        
        /// <summary>
        /// Adds a record for a Field Shooting score
        /// </summary>
        /// <param name="hits">Recorded number of hits to add</param>
        /// <param name="targets">Recorded number of targets hit to add</param>
        public void Add(int hits, int targets)
        {
            Add(hits, targets, 0);
        }
        
        /// <summary>
        /// Adds a record for a Field Shooting score with additional points value
        /// </summary>
        /// <param name="hits">Recorded number of hits to add</param>
        /// <param name="targets">Recorded number of targets hit to add</param>
        /// <param name="points">Recorded number of points to add</param>
        public void Add(int hits, int targets, int points)
        {
            Score poScore = new Score(ScoreType.Field);
            
            poScore.Hits = hits;
            poScore.Targets = targets;
            poScore.Points = points;
            moScores.Add(poScore);
        }
        
        /// <summary>
        /// Adds a predefined score record
        /// </summary>
        /// <param name="score">Predefined record</param>
        public void Add(Score score)
        {
            moScores.Add(score);
        }
        
        /// <summary>
        /// Updates a specified score record, replacing it with the supplied one
        /// </summary>
        /// <param name="station">Index of the record to update</param>
        /// <param name="score">Predefined record</param>
        public void Update(int station, Score score)
        {
            if (station <= moScores.Count)
            {
                moScores.Insert(station - 1, score);
                moScores.RemoveAt(station);
            }
        }
        
        /// <summary>
        /// Returns the number of score records in this set
        /// </summary>
        public int Count
        {
            get
            {
                if (moScores != null)
                {
                    return moScores.Count;
                }
                else
                {
                    return 0;
                }
            }
        }
        
        /// <summary>
        /// Returns the record type for the indicated index
        /// </summary>
        /// <param name="station">Index of the record to return</param>
        /// <returns>Record type</returns>
        public ScoreType Type(int station)
        {
            ScoreType peType = ScoreType.None;
            
            if (station <= moScores.Count)
            {
                peType = moScores.ToArray()[station - 1].Type;
            }
            
            return peType;
        }
        
        /// <summary>
        /// Returns the recorded number of points for the indicated index
        /// </summary>
        /// <param name="station">Index of the record to return</param>
        /// <returns>Number of points</returns>
        public int Points(int station)
        {
            int piPoints = 0;
            
            if (station <= moScores.Count)
            {
                piPoints = moScores.ToArray()[station - 1].Points;
            }
            
            return piPoints;
        }

        /// <summary>
        /// Returns the sum total number of points for this set
        /// </summary>
        public int TotalPoints
        {
            get
            {
                int piTotal = 0;
                
                foreach (Score poScore in moScores)
                {
                    piTotal += poScore.Points;
                }
                
                return piTotal;
            }
        }
        
        /// <summary>
        /// Returns the recorded number of hits for the indicated index
        /// </summary>
        /// <param name="station">Index of the record to return</param>
        /// <returns>Number of hits</returns>
        public int Hits(int station)
        {
            int piHits = 0;
            
            if (station <= moScores.Count)
            {
                piHits = moScores.ToArray()[station - 1].Hits;
            }
            
            return piHits;
        }

        /// <summary>
        /// Returns the sum total number of hits for this set
        /// </summary>
        public int TotalHits
        {
            get
            {
                int piTotal = 0;
                
                foreach (Score poScore in moScores)
                {
                    piTotal += poScore.Hits;
                }
                
                return piTotal;
            }
        }
        
        /// <summary>
        /// Returns the recorded number of targets hit for the indicated index
        /// </summary>
        /// <param name="station">Index of the record to return</param>
        /// <returns>Number of targets hit</returns>
        public int Targets(int station)
        {
            int piTargets = 0;
            
            if (station <= moScores.Count)
            {
                piTargets = moScores.ToArray()[station - 1].Targets;
            }
            
            return piTargets;
        }
        
        /// <summary>
        /// Returns the sum total number of targets hit for this set
        /// </summary>
        public int TotalTargets
        {
            get
            {
                int piTotal = 0;
                
                foreach (Score poScore in moScores)
                {
                    piTotal += poScore.Targets;
                }
                
                return piTotal;
            }
        }
    }
}