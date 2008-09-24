/*
 * Created by SharpDevelop.
 * User: johan
 * Date: 2008-09-23
 * Time: 14:14
 */
 
using System;
using System.Windows.Forms;
using Sagittarius.Lib;

namespace Sagittarius.GUI
{
    /// <summary>
    /// Class with program entry point.
    /// </summary>
    internal sealed class Program
    {
        /// <summary>
        /// Program entry point.
        /// </summary>
        [STAThread]
        private static void Main(string[] args)
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new MainForm());
        }
        
    }
}