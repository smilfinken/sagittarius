/*
 * Created by SharpDevelop.
 * User: johan
 * Date: 2008-09-23
 * Time: 14:24
 */
using System;

namespace Sagittarius.Lib
{
    /// <summary>
    /// Indicates the type of scoring used for this score record
    /// 
    /// Available types are
    ///   None - represents an uninitialised value
    ///   Field - Field Shooting score record
    ///   Target - Target Shooting score record
    ///   PPC - PPC match score record
    /// </summary>
    public enum ScoreType
    {
        /// <summary>
        /// represents an uninitialised value
        /// </summary>
        None,
        /// <summary>
        /// Field Shooting score record
        /// </summary>
        Field,
        /// <summary>
        /// Target Shooting score record
        /// </summary>
        Target,
        /// <summary>
        /// PPC match score record
        /// </summary>
        PPC
    }
    
    /// <summary>
    /// Holds a single score data.
    /// </summary>
    public class Score
    {
        private ScoreType meScoreType;
        private int miPoints;
        private int miHits;
        private int miTargets;
        
        private void Clear()
        {
            meScoreType = 0;
            miPoints = 0;
            miHits = 0;
            miTargets = 0;
        }
        
        /// <summary>
        /// Class constructor which generates an empty score record
        /// </summary>
        public Score()
        {
            Clear();
        }

        /// <summary>
        /// Class constructor which generates an empty score record of the selected type
        /// </summary>
        /// <param name="type">Record type</param>
        public Score(ScoreType type)
        {
            Clear();
            meScoreType = type;
        }
        
        /// <summary>
        /// Gets or sets the type for this record
        /// </summary>
        public ScoreType Type
        {
            get
            {
                return meScoreType;
            }
            
            set
            {
                meScoreType = value;
            }
        }
        
        /// <summary>
        /// Gets or sets the recorded number of points for this record
        /// </summary>
        public int Points
        {
            get
            {
                return miPoints;
            }
            
            set
            {
                miPoints = value;
            }
        }
        
        /// <summary>
        /// Gets or sets the recorded number of hits for this record
        /// </summary>
        public int Hits
        {
            get
            {
                return miHits;
            }
            
            set
            {
                miHits = value;
            }
        }
        
        /// <summary>
        /// Gets or sets the recorded number of targets hit for this record
        /// </summary>
        public int Targets
        {
            get
            {
                return miTargets;
            }
            
            set
            {
                miTargets = value;
            }
        }
    }
}