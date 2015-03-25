using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using TCX.Configuration;

namespace OMSamples.Samples
{
    [SampleCode("dn_monitor")]
    [SampleDescription("Output CallUpdate events for all connected external DNs")]
    class DNmonitorSample : ISample
    {
        public void Run(params string[] args)
        {
                foreach (DN dn in PhoneSystem.Root.GetDN())
                {		    
                    ActiveConnection[] a = dn.GetActiveConnections();
                    if (a.Length > 0)
                    {
                        foreach (ActiveConnection ac in a)
                        {
			    if (!(ac.DN is ExternalLine) || (ac.Status != ConnectionStatus.Connected)) {
			      continue;
			    }
			    System.Console.WriteLine("<CallUpdate xmlns=\"http://pbxevents.jjinterna.com/model\">");
                            System.Console.WriteLine("  <callId>" + ac.CallID + "</callId>");
                            System.Console.WriteLine("  <callDuration>" + 
				(int)(DateTime.UtcNow - ac.LastChangeStatus).TotalSeconds + "</callDuration>");
                            System.Console.WriteLine("  <callingNumber>" + ac.InternalParty.Number + "</callingNumber>");
                            System.Console.WriteLine("  <calledNumber>" + ac.DialedNumber + "</calledNumber>");
		            System.Console.WriteLine("</CallUpdate>");
                        }
                    }
                }
        }
    }
}